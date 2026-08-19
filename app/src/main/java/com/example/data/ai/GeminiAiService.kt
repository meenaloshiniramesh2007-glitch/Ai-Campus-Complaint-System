package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AIAnalysisResult
import com.example.data.model.CampusConstants
import com.example.data.model.ComplaintEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun analyzeComplaint(
        title: String,
        description: String,
        location: String,
        existingComplaints: List<ComplaintEntity>
    ): AIAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        // First check duplicate locally/semantically
        val duplicateCheck = checkLocalDuplicate(title, description, location, existingComplaints)

        if (hasValidKey) {
            try {
                val prompt = """
                    You are an intelligent Campus Complaint Dispatcher AI for a university.
                    Analyze this student complaint and classify it accurately.

                    Title: "$title"
                    Description: "$description"
                    Location: "$location"

                    Select the best Category from: ${CampusConstants.CATEGORIES.joinToString()}
                    Select the Department from: ${CampusConstants.DEPARTMENTS.joinToString()}
                    Select Priority from: LOW, MEDIUM, HIGH, CRITICAL (Use CRITICAL for fire, sparks, safety danger, flooding, urgent hazards).
                    Sentiment: Frustrated, Urgent, Neutral, or Positive.

                    Return strictly a JSON object with this exact structure:
                    {
                      "category": "string",
                      "priority": "LOW|MEDIUM|HIGH|CRITICAL",
                      "department": "string",
                      "summary": "1 concise sentence summarizing the core issue",
                      "confidence": 0.95,
                      "sentiment": "string"
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contents = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            val parts = JSONArray().apply {
                                put(JSONObject().apply { put("text", prompt) })
                            }
                            put("parts", parts)
                        }
                        put(partObj)
                    }
                    put("contents", contents)
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("temperature", 0.2)
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody(jsonMediaType)
                val request = Request.Builder().url(url).post(body).build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val respObj = JSONObject(responseBody)
                        val candidates = respObj.optJSONArray("candidates")
                        val firstCandidate = candidates?.optJSONObject(0)
                        val content = firstCandidate?.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        val text = parts?.optJSONObject(0)?.optString("text")

                        if (!text.isNullOrBlank()) {
                            val cleanText = text.replace("```json", "").replace("```", "").trim()
                            val parsed = JSONObject(cleanText)
                            val cat = parsed.optString("category", "General Administration")
                            val pri = parsed.optString("priority", "MEDIUM")
                            val dept = parsed.optString("department", "General Administration")
                            val sum = parsed.optString("summary", title)
                            val conf = parsed.optDouble("confidence", 0.94).toFloat()
                            val sent = parsed.optString("sentiment", "Urgent")

                            return@withContext AIAnalysisResult(
                                category = fixCategory(cat),
                                priority = fixPriority(pri),
                                department = fixDepartment(dept, cat),
                                summary = sum,
                                confidence = conf,
                                sentiment = sent,
                                potentialDuplicateId = duplicateCheck?.first,
                                duplicateNotice = duplicateCheck?.second
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("GeminiAiService", "Gemini API call failed, falling back to smart campus NLP: ${e.message}")
            }
        }

        // Smart Offline Campus Rule-Based NLP Engine
        return@withContext analyzeWithSmartRules(title, description, location, duplicateCheck)
    }

    suspend fun generateAiSuggestedResponse(
        complaint: ComplaintEntity,
        actionType: String = "Status Update"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidKey) {
            try {
                val prompt = """
                    You are an official university administrative representative responding to a student complaint.
                    Write a professional, courteous, and reassuring 2-3 sentence official response to the student.

                    Complaint Title: "${complaint.title}"
                    Description: "${complaint.description}"
                    Department: "${complaint.department}"
                    Current Status: "${complaint.status}"
                    Priority: "${complaint.priority}"
                    Action Context: "$actionType"

                    Make it sound empathetic, professional, and clear about the next steps.
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contents = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            val parts = JSONArray().apply {
                                put(JSONObject().apply { put("text", prompt) })
                            }
                            put("parts", parts)
                        }
                        put(partObj)
                    }
                    put("contents", contents)
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody(jsonMediaType)
                val request = Request.Builder().url(url).post(body).build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val respObj = JSONObject(responseBody)
                        val text = respObj.optJSONArray("candidates")
                            ?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text")

                        if (!text.isNullOrBlank()) {
                            return@withContext text.trim()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("GeminiAiService", "Gemini response suggestion failed: ${e.message}")
            }
        }

        // Rule-based official response generator
        return@withContext when (complaint.status) {
            "IN_PROGRESS" -> "Thank you for reporting this issue. Our ${complaint.department} technical team has been assigned to ticket #${complaint.id} and is currently working on site to resolve this swiftly."
            "RESOLVED" -> "The ${complaint.department} has completed repairs for ticket #${complaint.id}. Please inspect the resolution and provide your feedback rating to help us maintain campus standards."
            "PENDING" -> "Your complaint has been acknowledged by ${complaint.department}. We have prioritized this as ${complaint.priority} and a technician will be dispatched shortly."
            else -> "We have received your campus ticket regarding '${complaint.title}'. Our dispatch system has routed this to the ${complaint.department} for immediate evaluation."
        }
    }

    private fun checkLocalDuplicate(
        title: String,
        description: String,
        location: String,
        existingComplaints: List<ComplaintEntity>
    ): Pair<Long, String>? {
        val query = "$title $description".lowercase()
        val queryWords = query.split("\\s+".toRegex()).filter { it.length > 3 }.toSet()

        for (item in existingComplaints) {
            if (item.status == "RESOLVED" || item.status == "CLOSED") continue

            val target = "${item.title} ${item.description}".lowercase()
            val targetWords = target.split("\\s+".toRegex()).filter { it.length > 3 }.toSet()

            val intersection = queryWords.intersect(targetWords)
            val matchRatio = if (queryWords.isNotEmpty()) intersection.size.toFloat() / queryWords.size.toFloat() else 0f

            val sameLocation = location.isNotBlank() && item.location.equals(location, ignoreCase = true)

            if (matchRatio >= 0.45f || (sameLocation && matchRatio >= 0.30f)) {
                return Pair(
                    item.id,
                    "Potential duplicate detected: Similar issue (#${item.id} - \"${item.title}\") is already active in ${item.department}."
                )
            }
        }
        return null
    }

    private fun analyzeWithSmartRules(
        title: String,
        description: String,
        location: String,
        duplicateCheck: Pair<Long, String>?
    ): AIAnalysisResult {
        val text = "$title $description $location".lowercase()

        // 1. Priority Detection
        val isCritical = listOf("spark", "fire", "shock", "smoke", "burning", "flood", "emergency", "danger", "burst", "explosion", "injury")
            .any { text.contains(it) }

        val isHigh = listOf("leak", "broken pipe", "no water", "blackout", "unattended", "theft", "gateway timeout", "unreachable", "dark walkway", "exam", "deadline", "urgent")
            .any { text.contains(it) }

        val isLow = listOf("slow", "schedule", "delayed", "flicker", "paint", "suggestion", "dust", "cleaning routine")
            .any { text.contains(it) }

        val priority = when {
            isCritical -> "CRITICAL"
            isHigh -> "HIGH"
            isLow -> "LOW"
            else -> "MEDIUM"
        }

        // 2. Category & Department Routing
        val (category, department) = when {
            text.contains("wi-fi") || text.contains("wifi") || text.contains("internet") || text.contains("router") || text.contains("network") || text.contains("dns") ->
                Pair("Wi-Fi / Internet", "IT Department")

            text.contains("spark") || text.contains("switchboard") || text.contains("breaker") || text.contains("electric") || text.contains("power cut") || text.contains("light") || text.contains("wiring") ->
                Pair("Electricity", "Electrical Maintenance")

            text.contains("leak") || text.contains("pipe") || text.contains("plumb") || text.contains("drain") || text.contains("tap") || text.contains("flush") || text.contains("water") ->
                Pair("Plumbing", "Facilities & Plumbing")

            text.contains("projector") || text.contains("bench") || text.contains("blackboard") || text.contains("podium") || text.contains("classroom") || text.contains("lecture") || text.contains("speaker") ->
                Pair("Classroom", "IT Department")

            text.contains("hostel") || text.contains("room") || text.contains("bed") || text.contains("warden") || text.contains("dorm") || text.contains("cooler") ->
                Pair("Hostel", "Hostel Administration")

            text.contains("canteen") || text.contains("food") || text.contains("hygiene") || text.contains("mess") || text.contains("juice") || text.contains("cafeteria") || text.contains("snack") ->
                Pair("Canteen", "Canteen Management")

            text.contains("bus") || text.contains("shuttle") || text.contains("driver") || text.contains("transit") || text.contains("parking") || text.contains("transport") ->
                Pair("Transport", "Transport Department")

            text.contains("security") || text.contains("guard") || text.contains("gate") || text.contains("theft") || text.contains("trespass") || text.contains("cctv") || text.contains("barrier") ->
                Pair("Security", "Campus Security")

            text.contains("lab") || text.contains("equipment") || text.contains("chemical") || text.contains("oscilloscope") || text.contains("microscope") ->
                Pair("Laboratory", "Academic Office")

            text.contains("library") || text.contains("book") || text.contains("reading room") || text.contains("journal") || text.contains("librarian") ->
                Pair("Library", "Library Administration")

            text.contains("gym") || text.contains("sport") || text.contains("court") || text.contains("ground") || text.contains("football") || text.contains("badminton") ->
                Pair("Sports", "Sports Directorate")

            text.contains("exam") || text.contains("portal") || text.contains("assignment") || text.contains("grade") || text.contains("attendance") || text.contains("professor") || text.contains("syllabus") ->
                Pair("Academic", "Academic Office")

            text.contains("trash") || text.contains("dirty") || text.contains("clean") || text.contains("garbage") || text.contains("smell") || text.contains("dustbin") ->
                Pair("Cleanliness", "Facilities & Plumbing")

            else -> Pair("Infrastructure", "General Administration")
        }

        // 3. Sentiment Analysis
        val sentiment = when {
            isCritical -> "Critical Danger"
            text.contains("angry") || text.contains("frustrated") || text.contains("terrible") || text.contains("awful") || text.contains("repeated") || text.contains("again") -> "Frustrated"
            priority == "HIGH" || priority == "CRITICAL" -> "Urgent"
            else -> "Neutral"
        }

        // 4. Summary
        val summary = when {
            title.length in 10..80 -> "$category issue: $title"
            description.length > 60 -> description.take(65).trimEnd() + "..."
            else -> "$category concern reported at $location."
        }

        return AIAnalysisResult(
            category = category,
            priority = priority,
            department = department,
            summary = summary,
            confidence = if (isCritical || isHigh) 0.96f else 0.92f,
            sentiment = sentiment,
            potentialDuplicateId = duplicateCheck?.first,
            duplicateNotice = duplicateCheck?.second
        )
    }

    private fun fixCategory(cat: String): String {
        return CampusConstants.CATEGORIES.firstOrNull { it.equals(cat, ignoreCase = true) }
            ?: CampusConstants.CATEGORIES.firstOrNull { cat.contains(it, ignoreCase = true) }
            ?: "Infrastructure"
    }

    private fun fixPriority(pri: String): String {
        return when (pri.uppercase().trim()) {
            "CRITICAL", "EMERGENCY", "URGENT" -> "CRITICAL"
            "HIGH" -> "HIGH"
            "LOW" -> "LOW"
            else -> "MEDIUM"
        }
    }

    private fun fixDepartment(dept: String, cat: String): String {
        return CampusConstants.DEPARTMENTS.firstOrNull { it.equals(dept, ignoreCase = true) }
            ?: when (cat) {
                "Wi-Fi / Internet", "Classroom" -> "IT Department"
                "Electricity" -> "Electrical Maintenance"
                "Plumbing", "Cleanliness" -> "Facilities & Plumbing"
                "Hostel" -> "Hostel Administration"
                "Canteen" -> "Canteen Management"
                "Transport" -> "Transport Department"
                "Security" -> "Campus Security"
                "Academic", "Laboratory" -> "Academic Office"
                "Library" -> "Library Administration"
                "Sports" -> "Sports Directorate"
                else -> "General Administration"
            }
    }
}
