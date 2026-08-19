package com.example.data.model

enum class UserRole(val label: String) {
    STUDENT("Student"),
    ADMIN("Administrator"),
    STAFF("Department Staff")
}

enum class ComplaintPriority(val label: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    CRITICAL("Critical", 4);

    companion object {
        fun fromString(value: String): ComplaintPriority {
            return when (value.uppercase().trim()) {
                "CRITICAL", "URGENT", "EMERGENCY" -> CRITICAL
                "HIGH" -> HIGH
                "MEDIUM", "MODERATE" -> MEDIUM
                else -> LOW
            }
        }
    }
}

enum class ComplaintStatus(val label: String, val step: Int) {
    SUBMITTED("Submitted", 1),
    PENDING("Under Review", 2),
    IN_PROGRESS("In Progress", 3),
    RESOLVED("Resolved", 4),
    CLOSED("Closed", 5),
    REOPENED("Reopened", 3);

    companion object {
        fun fromString(value: String): ComplaintStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: SUBMITTED
        }
    }
}

object CampusConstants {
    val CATEGORIES = listOf(
        "Academic",
        "Classroom",
        "Laboratory",
        "Hostel",
        "Canteen",
        "Transport",
        "Wi-Fi / Internet",
        "Electricity",
        "Plumbing",
        "Cleanliness",
        "Security",
        "Library",
        "Sports",
        "Infrastructure",
        "Other"
    )

    val DEPARTMENTS = listOf(
        "IT Department",
        "Electrical Maintenance",
        "Facilities & Plumbing",
        "Hostel Administration",
        "Canteen Management",
        "Transport Department",
        "Campus Security",
        "Academic Office",
        "Library Administration",
        "Sports Directorate",
        "General Administration"
    )

    val LOCATIONS = listOf(
        "Block A - Computer Science",
        "Block B - Electronics Wing",
        "Block C - Mechanical Workshop",
        "Main Academic Building",
        "Boys Hostel Block 1",
        "Boys Hostel Block 2",
        "Girls Hostel Block A",
        "Girls Hostel Block B",
        "Central Library",
        "Central Canteen",
        "Sports Complex & Gymnasium",
        "Campus Bus Terminal",
        "Admin Block - 2nd Floor",
        "Chemistry / Physics Lab"
    )
}
