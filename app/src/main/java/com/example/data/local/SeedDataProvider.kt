package com.example.data.local

import com.example.data.model.*

object SeedDataProvider {
    suspend fun populateInitialData(db: AppDatabase) {
        val now = System.currentTimeMillis()
        val oneHour = 3600_000L
        val oneDay = 86400_000L

        // 1. Initial Users
        val users = listOf(
            UserEntity(
                id = 1,
                name = "Alex Rivera",
                email = "alex.rivera@campus.edu",
                role = "STUDENT",
                studentOrStaffId = "STU-2024-8819",
                department = "Computer Science",
                avatarUrl = ""
            ),
            UserEntity(
                id = 2,
                name = "Dr. Sarah Jenkins",
                email = "sarah.jenkins@campus.edu",
                role = "ADMIN",
                studentOrStaffId = "ADM-1002",
                department = "Dean of Student Affairs",
                avatarUrl = ""
            ),
            UserEntity(
                id = 3,
                name = "David Miller",
                email = "david.m@campus.edu",
                role = "STAFF",
                studentOrStaffId = "STF-IT-401",
                department = "IT Department",
                avatarUrl = ""
            ),
            UserEntity(
                id = 4,
                name = "Rajesh Kumar",
                email = "rajesh.k@campus.edu",
                role = "STAFF",
                studentOrStaffId = "STF-ELEC-205",
                department = "Electrical Maintenance",
                avatarUrl = ""
            ),
            UserEntity(
                id = 5,
                name = "Elena Rostova",
                email = "elena.r@campus.edu",
                role = "STAFF",
                studentOrStaffId = "STF-FAC-309",
                department = "Facilities & Plumbing",
                avatarUrl = ""
            )
        )
        db.userDao().insertAll(users)

        // 2. Initial Complaints
        val complaints = listOf(
            ComplaintEntity(
                id = 101,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Electrical sparks from laboratory switchboard",
                description = "During the Advanced Electronics lab session, heavy electrical sparks and burning plastic smell started coming from switchboard #4 near the oscilloscope station. We immediately turned off the bench breaker.",
                category = "Electricity",
                priority = "CRITICAL",
                department = "Electrical Maintenance",
                location = "Block B - Electronics Wing",
                imageUrl = null,
                status = "IN_PROGRESS",
                aiSummary = "Hazardous electrical sparking and burning smell from lab switchboard #4.",
                aiConfidence = 0.98f,
                sentiment = "Critical Danger",
                assignedStaffId = 4,
                assignedStaffName = "Rajesh Kumar",
                staffRemarks = "Breaker isolated. Replacement parts requisitioned for emergency repair.",
                createdAt = now - (2 * oneHour),
                updatedAt = now - (30 * 60_000L)
            ),
            ComplaintEntity(
                id = 102,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Wi-Fi down across 3rd floor classrooms",
                description = "The Wi-Fi access points in Block A rooms 301 to 308 are completely unreachable since this morning. Students are unable to access lecture slides or submit assignments online.",
                category = "Wi-Fi / Internet",
                priority = "HIGH",
                department = "IT Department",
                location = "Block A - Computer Science",
                imageUrl = null,
                status = "PENDING",
                aiSummary = "Campus Wi-Fi connectivity blackout affecting multiple 3rd floor classrooms.",
                aiConfidence = 0.95f,
                sentiment = "Frustrated",
                assignedStaffId = 3,
                assignedStaffName = "David Miller",
                staffRemarks = "Investigating switch PoE failure in Rack 3.",
                createdAt = now - (4 * oneHour),
                updatedAt = now - (2 * oneHour)
            ),
            ComplaintEntity(
                id = 103,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Hostel 2nd floor bathroom severe water leakage",
                description = "Continuous water overflowing from ceiling pipe onto the floor in Boys Hostel Block 1, second floor east wing restroom. Slippery hazard and water accumulating quickly.",
                category = "Plumbing",
                priority = "HIGH",
                department = "Facilities & Plumbing",
                location = "Boys Hostel Block 1",
                imageUrl = null,
                status = "IN_PROGRESS",
                aiSummary = "Ceiling pipe leakage causing flooding hazard in hostel east wing restroom.",
                aiConfidence = 0.94f,
                sentiment = "Urgent",
                assignedStaffId = 5,
                assignedStaffName = "Elena Rostova",
                staffRemarks = "Main valve shut off temporarily. Pipe weld in progress.",
                createdAt = now - (8 * oneHour),
                updatedAt = now - (1 * oneHour)
            ),
            ComplaintEntity(
                id = 104,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "CS Seminar Hall Projector lamp failure",
                description = "The primary ceiling projector in CS Hall 101 flickers orange and turns off after 2 minutes. Faculty guest lecture is scheduled for tomorrow at 10 AM.",
                category = "Classroom",
                priority = "MEDIUM",
                department = "IT Department",
                location = "Block A - Computer Science",
                imageUrl = null,
                status = "RESOLVED",
                aiSummary = "Projector lamp failure in CS Hall 101 ahead of scheduled guest lecture.",
                aiConfidence = 0.92f,
                sentiment = "Urgent",
                assignedStaffId = 3,
                assignedStaffName = "David Miller",
                staffRemarks = "Replaced projector lamp and tested HDMI input successfully.",
                resolutionProofNote = "Installed OEM bulb assembly. Calibrated brightness to 4500 lumens. Confirmed 4K 60Hz display.",
                createdAt = now - (1 * oneDay),
                updatedAt = now - (3 * oneHour),
                resolvedAt = now - (3 * oneHour)
            ),
            ComplaintEntity(
                id = 105,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Canteen juice counter hygiene concern",
                description = "Observed fruit flies and standing dirty water beneath the fresh juice blender area at the central campus canteen. Counters need sanitization and proper drain clearing.",
                category = "Canteen",
                priority = "MEDIUM",
                department = "Canteen Management",
                location = "Central Canteen",
                imageUrl = null,
                status = "SUBMITTED",
                aiSummary = "Hygiene and sanitation concern regarding juice counter area in Central Canteen.",
                aiConfidence = 0.91f,
                sentiment = "Frustrated",
                createdAt = now - (5 * oneHour),
                updatedAt = now - (5 * oneHour)
            ),
            ComplaintEntity(
                id = 106,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Campus evening shuttle Bus #4 delay",
                description = "Route 4 evening shuttle has been consistently 35-45 minutes late during peak dismissal hours (5:30 PM), causing overcrowding and missed transit connections.",
                category = "Transport",
                priority = "LOW",
                department = "Transport Department",
                location = "Campus Bus Terminal",
                imageUrl = null,
                status = "RESOLVED",
                aiSummary = "Persistent scheduling delays on Route 4 evening campus transit shuttle.",
                aiConfidence = 0.88f,
                sentiment = "Neutral",
                assignedStaffId = 2,
                assignedStaffName = "Admin Office",
                resolutionProofNote = "Adjusted route timetable and added a backup 30-passenger van during peak 5:00-6:30 PM window.",
                createdAt = now - (3 * oneDay),
                updatedAt = now - (1 * oneDay),
                resolvedAt = now - (1 * oneDay)
            ),
            ComplaintEntity(
                id = 107,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Security guard missing at North Pedestrian Gate",
                description = "The North Gate entry boom barrier was left unattended between 8:00 PM and 10:00 PM on Tuesday. Non-students were seen walking in without badge checks.",
                category = "Security",
                priority = "HIGH",
                department = "Campus Security",
                location = "Main Academic Building",
                imageUrl = null,
                status = "IN_PROGRESS",
                aiSummary = "Unattended security checkpoint and access control lapse at North Gate.",
                aiConfidence = 0.96f,
                sentiment = "Urgent",
                assignedStaffId = 2,
                assignedStaffName = "Security Chief",
                staffRemarks = "Security patrol shift rotation reviewed. Backup guard posted immediately.",
                createdAt = now - (18 * oneHour),
                updatedAt = now - (6 * oneHour)
            ),
            ComplaintEntity(
                id = 108,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Air conditioning leaking in Central Library Quiet Zone",
                description = "The ceiling cassette AC in the 2nd floor silent study room is dripping water onto study desks #12 and #13. Books on the lower rack were almost damaged.",
                category = "Cleanliness",
                priority = "MEDIUM",
                department = "Facilities & Plumbing",
                location = "Central Library",
                imageUrl = null,
                status = "REOPENED",
                aiSummary = "Dripping AC unit causing water damage hazard in library silent study zone.",
                aiConfidence = 0.93f,
                sentiment = "Frustrated",
                assignedStaffId = 5,
                assignedStaffName = "Elena Rostova",
                staffRemarks = "Condensation drain was cleared yesterday but student reported re-leakage.",
                createdAt = now - (2 * oneDay),
                updatedAt = now - (4 * oneHour)
            ),
            ComplaintEntity(
                id = 109,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Broken cable machine pulley in campus gym",
                description = "The high cable pulley cable in the strength training room has frayed wires and jammed pulley wheel. Potential safety risk if weight snaps.",
                category = "Sports",
                priority = "MEDIUM",
                department = "Sports Directorate",
                location = "Sports Complex & Gymnasium",
                imageUrl = null,
                status = "CLOSED",
                aiSummary = "Damaged cable and pulley mechanism on gym resistance machine.",
                aiConfidence = 0.94f,
                sentiment = "Urgent",
                assignedStaffId = 2,
                assignedStaffName = "Sports Facility Mgr",
                resolutionProofNote = "Steel cable and pulley wheel replaced with brand-new heavy-duty assembly. Safety certified.",
                createdAt = now - (5 * oneDay),
                updatedAt = now - (2 * oneDay),
                resolvedAt = now - (2 * oneDay)
            ),
            ComplaintEntity(
                id = 110,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Water cooler dispensing warm water in Girls Hostel",
                description = "The main RO water filtration cooler on the ground floor lobby of Girls Hostel Block A is not chilling and making a loud rattling compressor sound.",
                category = "Hostel",
                priority = "MEDIUM",
                department = "Hostel Administration",
                location = "Girls Hostel Block A",
                imageUrl = null,
                status = "PENDING",
                aiSummary = "Malfunctioning water cooler and compressor noise in hostel lobby.",
                aiConfidence = 0.90f,
                sentiment = "Neutral",
                assignedStaffId = 4,
                assignedStaffName = "Rajesh Kumar",
                staffRemarks = "Compressor relay inspection scheduled for this afternoon.",
                createdAt = now - (12 * oneHour),
                updatedAt = now - (2 * oneHour)
            ),
            ComplaintEntity(
                id = 111,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Pathway solar streetlights dark along South Quad",
                description = "Three consecutive solar LED poles along the dark walkway between Block C and Student Center are not turning on at night, leaving the path poorly lit.",
                category = "Electricity",
                priority = "LOW",
                department = "Electrical Maintenance",
                location = "Block C - Mechanical Workshop",
                imageUrl = null,
                status = "SUBMITTED",
                aiSummary = "Pathway lighting failure on South Quad pedestrian walkway.",
                aiConfidence = 0.89f,
                sentiment = "Neutral",
                createdAt = now - (1 * oneHour),
                updatedAt = now - (1 * oneHour)
            ),
            ComplaintEntity(
                id = 112,
                userId = 1,
                studentName = "Alex Rivera",
                studentIdNumber = "STU-2024-8819",
                title = "Online Assignment Portal submission timeout error",
                description = "When uploading multi-part PDF assignments over 10MB in the CS portal, students get HTTP 504 gateway timeout. Major submission deadline tonight at midnight.",
                category = "Academic",
                priority = "HIGH",
                department = "Academic Office",
                location = "Main Academic Building",
                imageUrl = null,
                status = "IN_PROGRESS",
                aiSummary = "Server timeout on student assignment submission portal before tonight's deadline.",
                aiConfidence = 0.96f,
                sentiment = "Frustrated",
                assignedStaffId = 3,
                assignedStaffName = "David Miller",
                staffRemarks = "Nginx upload size limit and timeout parameter increased to 50MB/300s. Testing deployment.",
                createdAt = now - (3 * oneHour),
                updatedAt = now - (45 * 60_000L)
            )
        )
        db.complaintDao().insertAll(complaints)

        // 3. Initial Comments
        val comments = listOf(
            ComplaintCommentEntity(
                id = 1,
                complaintId = 101,
                userId = 1,
                userName = "Alex Rivera (Student)",
                userRole = "STUDENT",
                comment = "We noticed smoke rising behind the panel. Lab technician was notified immediately.",
                createdAt = now - (110 * 60_000L)
            ),
            ComplaintCommentEntity(
                id = 2,
                complaintId = 101,
                userId = 4,
                userName = "Rajesh Kumar (Electrical Staff)",
                userRole = "STAFF",
                comment = "I have arrived on site and cut off main breaker 4B. Replacing the burnt 32A contactor now.",
                createdAt = now - (45 * 60_000L)
            ),
            ComplaintCommentEntity(
                id = 3,
                complaintId = 104,
                userId = 3,
                userName = "David Miller (IT Staff)",
                userRole = "STAFF",
                comment = "New bulb unit installed and tested with both HDMI and Wireless casting. Working properly at 1080p.",
                createdAt = now - (3 * oneHour)
            ),
            ComplaintCommentEntity(
                id = 4,
                complaintId = 108,
                userId = 1,
                userName = "Alex Rivera (Student)",
                userRole = "STUDENT",
                comment = "Reopening this because water started dripping again during the 2 PM study session. Please inspect the main tray.",
                createdAt = now - (4 * oneHour)
            )
        )
        db.commentDao().insertAll(comments)

        // 4. Initial History
        val histories = listOf(
            ComplaintHistoryEntity(
                id = 1,
                complaintId = 101,
                oldStatus = "SUBMITTED",
                newStatus = "PENDING",
                changedBy = "AI Automated Dispatcher",
                remarks = "AI categorized as Electricity with CRITICAL priority. Auto-routed to Electrical Maintenance.",
                timestamp = now - (118 * 60_000L)
            ),
            ComplaintHistoryEntity(
                id = 2,
                complaintId = 101,
                oldStatus = "PENDING",
                newStatus = "IN_PROGRESS",
                changedBy = "Rajesh Kumar (Staff)",
                remarks = "Accepted emergency work ticket. Dispatched to Block B Electronics Lab.",
                timestamp = now - (60 * 60_000L)
            ),
            ComplaintHistoryEntity(
                id = 3,
                complaintId = 104,
                oldStatus = "IN_PROGRESS",
                newStatus = "RESOLVED",
                changedBy = "David Miller (IT Staff)",
                remarks = "Replaced bulb and verified audio/video output.",
                timestamp = now - (3 * oneHour)
            ),
            ComplaintHistoryEntity(
                id = 4,
                complaintId = 108,
                oldStatus = "RESOLVED",
                newStatus = "REOPENED",
                changedBy = "Alex Rivera (Student)",
                remarks = "Leakage recurred from upper drain fitting.",
                timestamp = now - (4 * oneHour)
            )
        )
        db.historyDao().insertAll(histories)

        // 5. Initial Notifications
        val notifications = listOf(
            NotificationEntity(
                id = 1,
                userId = 1,
                complaintId = 101,
                title = "⚡ Priority Alert: Ticket #101 In Progress",
                message = "Electrical Maintenance staff Rajesh Kumar has accepted your complaint and is on site.",
                isRead = false,
                createdAt = now - (60 * 60_000L)
            ),
            NotificationEntity(
                id = 2,
                userId = 1,
                complaintId = 104,
                title = "✅ Resolution Ready: Ticket #104",
                message = "Your CS Hall projector complaint has been marked as Resolved. Please verify and rate the resolution.",
                isRead = false,
                createdAt = now - (3 * oneHour)
            ),
            NotificationEntity(
                id = 3,
                userId = 1,
                complaintId = 108,
                title = "🔄 Ticket #108 Reopened",
                message = "Facilities & Plumbing was notified regarding renewed leakage in the Central Library.",
                isRead = true,
                createdAt = now - (4 * oneHour)
            ),
            NotificationEntity(
                id = 4,
                userId = 0,
                complaintId = 112,
                title = "🚨 Academic Portal Issue Escalation",
                message = "High priority ticket #112 filed regarding assignment portal timeouts.",
                isRead = false,
                createdAt = now - (3 * oneHour)
            )
        )
        db.notificationDao().insertAll(notifications)

        // 6. Initial Rating
        val ratings = listOf(
            RatingEntity(
                id = 1,
                complaintId = 104,
                studentId = 1,
                rating = 5,
                feedback = "Very fast turnaround! Projector is working crisp and clear for tomorrow's guest lecture.",
                createdAt = now - (2 * oneHour)
            ),
            RatingEntity(
                id = 2,
                complaintId = 106,
                studentId = 1,
                rating = 4,
                feedback = "The backup shuttle van helped a lot yesterday evening.",
                createdAt = now - (12 * oneHour)
            )
        )
        db.ratingDao().insertRating(ratings[0])
        db.ratingDao().insertRating(ratings[1])
    }
}
