package com.dormitorymanager

data class Duty(
    val id: Long = 0,
    val studentId: Long,
    val date: String,
    val type: String = "normal",
    val notes: String? = null
)
