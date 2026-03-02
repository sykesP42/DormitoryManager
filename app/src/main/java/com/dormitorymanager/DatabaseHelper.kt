package com.dormitorymanager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dormitory.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_STUDENTS = "students"
        private const val TABLE_DUTIES = "duties"
        private const val TABLE_SETTINGS = "settings"

        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_COLOR = "color"
        private const val KEY_ORDER_INDEX = "order_index"
        private const val KEY_STUDENT_ID = "student_id"
        private const val KEY_DATE = "date"
        private const val KEY_TYPE = "type"
        private const val KEY_NOTES = "notes"
        private const val KEY_KEY = "key"
        private const val KEY_VALUE = "value"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createStudentsTable = """
            CREATE TABLE $TABLE_STUDENTS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NAME TEXT NOT NULL,
                $KEY_COLOR INTEGER NOT NULL,
                $KEY_ORDER_INDEX INTEGER NOT NULL
            )
        """.trimIndent()

        val createDutiesTable = """
            CREATE TABLE $TABLE_DUTIES (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_STUDENT_ID INTEGER NOT NULL,
                $KEY_DATE TEXT NOT NULL,
                $KEY_TYPE TEXT NOT NULL DEFAULT 'normal',
                $KEY_NOTES TEXT,
                FOREIGN KEY ($KEY_STUDENT_ID) REFERENCES $TABLE_STUDENTS ($KEY_ID)
            )
        """.trimIndent()

        val createSettingsTable = """
            CREATE TABLE $TABLE_SETTINGS (
                $KEY_KEY TEXT PRIMARY KEY,
                $KEY_VALUE TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createStudentsTable)
        db.execSQL(createDutiesTable)
        db.execSQL(createSettingsTable)

        val defaultStudents = listOf(
            Student(name = "室友1", color = 0xFF6750A4.toInt(), orderIndex = 0),
            Student(name = "室友2", color = 0xFF2196F3.toInt(), orderIndex = 1),
            Student(name = "室友3", color = 0xFF4CAF50.toInt(), orderIndex = 2),
            Student(name = "室友4", color = 0xFFFF9800.toInt(), orderIndex = 3),
            Student(name = "室友5", color = 0xFFE91E63.toInt(), orderIndex = 4),
            Student(name = "室友6", color = 0xFF9C27B0.toInt(), orderIndex = 5)
        )

        defaultStudents.forEach { student ->
            val values = ContentValues().apply {
                put(KEY_NAME, student.name)
                put(KEY_COLOR, student.color)
                put(KEY_ORDER_INDEX, student.orderIndex)
            }
            db.insert(TABLE_STUDENTS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DUTIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        onCreate(db)
    }

    fun getStudents(): List<Student> {
        val students = mutableListOf<Student>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_STUDENTS,
            null,
            null,
            null,
            null,
            null,
            "$KEY_ORDER_INDEX ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                students.add(
                    Student(
                        id = it.getLong(it.getColumnIndexOrThrow(KEY_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(KEY_NAME)),
                        color = it.getInt(it.getColumnIndexOrThrow(KEY_COLOR)),
                        orderIndex = it.getInt(it.getColumnIndexOrThrow(KEY_ORDER_INDEX))
                    )
                )
            }
        }
        return students
    }

    fun insertStudent(student: Student): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, student.name)
            put(KEY_COLOR, student.color)
            put(KEY_ORDER_INDEX, student.orderIndex)
        }
        return db.insert(TABLE_STUDENTS, null, values)
    }

    fun deleteAllStudents() {
        val db = writableDatabase
        db.delete(TABLE_STUDENTS, null, null)
        db.delete(TABLE_DUTIES, null, null)
    }

    fun initializeDuties() {
        setSetting("first_duty_date", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    fun getStudentById(id: Long): Student? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_STUDENTS,
            null,
            "$KEY_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return Student(
                    id = it.getLong(it.getColumnIndexOrThrow(KEY_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(KEY_NAME)),
                    color = it.getInt(it.getColumnIndexOrThrow(KEY_COLOR)),
                    orderIndex = it.getInt(it.getColumnIndexOrThrow(KEY_ORDER_INDEX))
                )
            }
        }
        return null
    }

    fun getDutyByDate(date: String): Pair<Student?, String?> {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DUTIES,
            null,
            "$KEY_DATE = ? AND $KEY_TYPE != ?",
            arrayOf(date, "cancelled"),
            null,
            null,
            "$KEY_ID DESC",
            "1"
        )

        cursor.use {
            if (it.moveToFirst()) {
                val studentId = it.getLong(it.getColumnIndexOrThrow(KEY_STUDENT_ID))
                val type = it.getString(it.getColumnIndexOrThrow(KEY_TYPE))
                return Pair(getStudentById(studentId), type)
            }
        }

        return calculateDutyForDate(date)
    }

    private fun calculateDutyForDate(date: String): Pair<Student?, String?> {
        val students = getStudents()
        if (students.isEmpty()) return Pair(null, null)

        val firstDutyDate = getSetting("first_duty_date")
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        if (firstDutyDate == null) {
            setSetting("first_duty_date", today)
            return Pair(students[0], "normal")
        }

        val firstDate = LocalDate.parse(firstDutyDate)
        val targetDate = LocalDate.parse(date)
        val daysDiff = targetDate.toEpochDay() - firstDate.toEpochDay()

        if (daysDiff < 0) {
            return Pair(students[0], "normal")
        }

        val index = (daysDiff % students.size).toInt()
        return Pair(students[index], "normal")
    }

    fun getDutyHistory(): List<DutyHistoryItem> {
        val items = mutableListOf<DutyHistoryItem>()
        val today = LocalDate.now()
        val firstDutyDate = getSetting("first_duty_date")?.let { LocalDate.parse(it) } ?: today

        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DUTIES,
            null,
            "$KEY_DATE <= ?",
            arrayOf(today.format(DateTimeFormatter.ISO_LOCAL_DATE)),
            null,
            null,
            "$KEY_DATE DESC, $KEY_ID DESC"
        )

        val existingDuties = mutableMapOf<String, DutyHistoryItem>()

        cursor.use {
            while (it.moveToNext()) {
                val dateStr = it.getString(it.getColumnIndexOrThrow(KEY_DATE))
                val studentId = it.getLong(it.getColumnIndexOrThrow(KEY_STUDENT_ID))
                val type = it.getString(it.getColumnIndexOrThrow(KEY_TYPE))
                val notes = it.getString(it.getColumnIndexOrThrow(KEY_NOTES))

                if (!existingDuties.containsKey(dateStr)) {
                    val date = LocalDate.parse(dateStr)
                    val student = getStudentById(studentId)
                    existingDuties[dateStr] = DutyHistoryItem(date, student, type, notes)
                }
            }
        }

        var currentDate = today
        val endDate = firstDutyDate.minusDays(1)
        
        while (!currentDate.isBefore(endDate) && items.size < 30) {
            val dateStr = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            if (existingDuties.containsKey(dateStr)) {
                items.add(existingDuties[dateStr]!!)
            } else {
                val (student, type) = getDutyByDate(dateStr)
                items.add(DutyHistoryItem(currentDate, student, type ?: "normal", null))
            }
            currentDate = currentDate.minusDays(1)
        }

        return items
    }

    fun insertDuty(duty: Duty): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_STUDENT_ID, duty.studentId)
            put(KEY_DATE, duty.date)
            put(KEY_TYPE, duty.type)
            put(KEY_NOTES, duty.notes)
        }
        return db.insert(TABLE_DUTIES, null, values)
    }

    fun getSetting(key: String): String? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_SETTINGS,
            null,
            "$KEY_KEY = ?",
            arrayOf(key),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(KEY_VALUE))
            }
        }
        return null
    }

    fun setSetting(key: String, value: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_KEY, key)
            put(KEY_VALUE, value)
        }
        db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }
}
