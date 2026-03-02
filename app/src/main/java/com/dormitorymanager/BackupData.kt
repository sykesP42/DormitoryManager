package com.dormitorymanager

data class BackupData(
    val dormitoryName: String,
    val dormitorySize: Int,
    val startDate: String?,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    val students: List<Student>,
    val duties: List<Duty> = emptyList(),
    val dutyRecords: List<DutyRecord> = emptyList()
)
