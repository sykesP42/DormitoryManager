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
        private const val DATABASE_VERSION = 3

        private const val TABLE_STUDENTS = "students"
        private const val TABLE_DUTIES = "duties"
        private const val TABLE_SETTINGS = "settings"
        private const val TABLE_DUTY_RECORDS = "duty_records"
        private const val TABLE_MEMOS = "memos"

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
        private const val KEY_COMPLETED = "completed"
        private const val KEY_COMPLETED_AT = "completed_at"
        private const val KEY_RATING = "rating"
        private const val KEY_REVIEW_NOTES = "review_notes"
        private const val KEY_LIKES = "likes"
        private const val KEY_TITLE = "title"
        private const val KEY_CONTENT = "content"
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

        val createDutyRecordsTable = """
            CREATE TABLE $TABLE_DUTY_RECORDS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_STUDENT_ID INTEGER NOT NULL,
                $KEY_DATE TEXT NOT NULL,
                $KEY_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $KEY_COMPLETED_AT TEXT,
                $KEY_RATING INTEGER DEFAULT 0,
                $KEY_REVIEW_NOTES TEXT,
                $KEY_LIKES INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY ($KEY_STUDENT_ID) REFERENCES $TABLE_STUDENTS ($KEY_ID),
                UNIQUE($KEY_STUDENT_ID, $KEY_DATE)
            )
        """.trimIndent()

        val createMemosTable = """
            CREATE TABLE $TABLE_MEMOS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_STUDENT_ID INTEGER,
                $KEY_TITLE TEXT NOT NULL,
                $KEY_CONTENT TEXT,
                $KEY_DATE TEXT NOT NULL,
                FOREIGN KEY ($KEY_STUDENT_ID) REFERENCES $TABLE_STUDENTS ($KEY_ID)
            )
        """.trimIndent()

        db.execSQL(createStudentsTable)
        db.execSQL(createDutiesTable)
        db.execSQL(createSettingsTable)
        db.execSQL(createDutyRecordsTable)
        db.execSQL(createMemosTable)

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
        if (oldVersion < 2) {
            val createDutyRecordsTable = """
                CREATE TABLE $TABLE_DUTY_RECORDS (
                    $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $KEY_STUDENT_ID INTEGER NOT NULL,
                    $KEY_DATE TEXT NOT NULL,
                    $KEY_COMPLETED INTEGER NOT NULL DEFAULT 0,
                    $KEY_COMPLETED_AT TEXT,
                    $KEY_RATING INTEGER DEFAULT 0,
                    $KEY_REVIEW_NOTES TEXT,
                    $KEY_LIKES INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY ($KEY_STUDENT_ID) REFERENCES $TABLE_STUDENTS ($KEY_ID),
                    UNIQUE($KEY_STUDENT_ID, $KEY_DATE)
                )
            """.trimIndent()
            db.execSQL(createDutyRecordsTable)
        }
        if (oldVersion < 3) {
            val createMemosTable = """
                CREATE TABLE $TABLE_MEMOS (
                    $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $KEY_STUDENT_ID INTEGER,
                    $KEY_TITLE TEXT NOT NULL,
                    $KEY_CONTENT TEXT,
                    $KEY_DATE TEXT NOT NULL,
                    FOREIGN KEY ($KEY_STUDENT_ID) REFERENCES $TABLE_STUDENTS ($KEY_ID)
                )
            """.trimIndent()
            db.execSQL(createMemosTable)
        }
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

    fun addStudent(student: Student): Long {
        return insertStudent(student)
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
                    val dutyRecord = getDutyRecord(studentId, dateStr)
                    existingDuties[dateStr] = DutyHistoryItem(
                        date, 
                        student, 
                        type, 
                        notes,
                        dutyRecord?.completed ?: false,
                        dutyRecord?.completedAt,
                        dutyRecord?.rating ?: 0,
                        dutyRecord?.likes ?: 0
                    )
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
                val dutyRecord = student?.let { getDutyRecord(it.id, dateStr) }
                items.add(DutyHistoryItem(
                    currentDate, 
                    student, 
                    type ?: "normal", 
                    null,
                    dutyRecord?.completed ?: false,
                    dutyRecord?.completedAt,
                    dutyRecord?.rating ?: 0,
                    dutyRecord?.likes ?: 0
                ))
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

    fun getStatistics(): Triple<Int, Int, Int> {
        val db = readableDatabase
        var swapCount = 0
        var timeoffCount = 0
        var makeupCount = 0

        val cursor: Cursor = db.query(
            TABLE_DUTIES,
            arrayOf(KEY_TYPE),
            null,
            null,
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                when (it.getString(it.getColumnIndexOrThrow(KEY_TYPE))) {
                    "swap" -> swapCount++
                    "timeoff" -> timeoffCount++
                    "makeup" -> makeupCount++
                }
            }
        }

        return Triple(swapCount, timeoffCount, makeupCount)
    }

    fun getStudentDutyCount(studentId: Long): Int {
        val today = LocalDate.now()
        val firstDutyDate = getSetting("first_duty_date")?.let { LocalDate.parse(it) } ?: today
        
        var count = 0
        var currentDate = firstDutyDate
        
        while (!currentDate.isAfter(today)) {
            val dateStr = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val (dutyStudent, _) = getDutyByDate(dateStr)
            if (dutyStudent?.id == studentId) {
                count++
            }
            currentDate = currentDate.plusDays(1)
        }
        
        return count
    }

    fun getTotalDays(): Int {
        val today = LocalDate.now()
        val firstDutyDate = getSetting("first_duty_date")?.let { LocalDate.parse(it) } ?: today
        return today.toEpochDay().toInt() - firstDutyDate.toEpochDay().toInt() + 1
    }

    fun getDutyRecord(studentId: Long, date: String): DutyRecord? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DUTY_RECORDS,
            null,
            "$KEY_STUDENT_ID = ? AND $KEY_DATE = ?",
            arrayOf(studentId.toString(), date),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return DutyRecord(
                    id = it.getLong(it.getColumnIndexOrThrow(KEY_ID)),
                    studentId = it.getLong(it.getColumnIndexOrThrow(KEY_STUDENT_ID)),
                    date = it.getString(it.getColumnIndexOrThrow(KEY_DATE)),
                    completed = it.getInt(it.getColumnIndexOrThrow(KEY_COMPLETED)) == 1,
                    completedAt = it.getString(it.getColumnIndexOrThrow(KEY_COMPLETED_AT)),
                    rating = it.getInt(it.getColumnIndexOrThrow(KEY_RATING)),
                    reviewNotes = it.getString(it.getColumnIndexOrThrow(KEY_REVIEW_NOTES)),
                    likes = it.getInt(it.getColumnIndexOrThrow(KEY_LIKES))
                )
            }
        }
        return null
    }

    fun saveDutyRecord(record: DutyRecord): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_STUDENT_ID, record.studentId)
            put(KEY_DATE, record.date)
            put(KEY_COMPLETED, if (record.completed) 1 else 0)
            put(KEY_COMPLETED_AT, record.completedAt)
            put(KEY_RATING, record.rating)
            put(KEY_REVIEW_NOTES, record.reviewNotes)
            put(KEY_LIKES, record.likes)
        }
        return db.insertWithOnConflict(TABLE_DUTY_RECORDS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun markDutyCompleted(studentId: Long, date: String, completedAt: String) {
        val existingRecord = getDutyRecord(studentId, date)
        val record = if (existingRecord != null) {
            existingRecord.copy(completed = true, completedAt = completedAt)
        } else {
            DutyRecord(
                id = 0,
                studentId = studentId,
                date = date,
                completed = true,
                completedAt = completedAt,
                rating = 0,
                reviewNotes = null,
                likes = 0
            )
        }
        saveDutyRecord(record)
    }

    fun updateRating(studentId: Long, date: String, rating: Int, reviewNotes: String?) {
        val existingRecord = getDutyRecord(studentId, date)
        val record = if (existingRecord != null) {
            existingRecord.copy(rating = rating, reviewNotes = reviewNotes)
        } else {
            DutyRecord(
                id = 0,
                studentId = studentId,
                date = date,
                completed = false,
                completedAt = null,
                rating = rating,
                reviewNotes = reviewNotes,
                likes = 0
            )
        }
        saveDutyRecord(record)
    }

    fun addLike(studentId: Long, date: String) {
        val existingRecord = getDutyRecord(studentId, date)
        val record = if (existingRecord != null) {
            existingRecord.copy(likes = existingRecord.likes + 1)
        } else {
            DutyRecord(
                id = 0,
                studentId = studentId,
                date = date,
                completed = false,
                completedAt = null,
                rating = 0,
                reviewNotes = null,
                likes = 1
            )
        }
        saveDutyRecord(record)
    }

    fun getStudentStats(studentId: Long): Pair<Int, Double> {
        val db = readableDatabase
        var completedCount = 0
        var totalRating = 0
        var ratingCount = 0

        val cursor: Cursor = db.query(
            TABLE_DUTY_RECORDS,
            null,
            "$KEY_STUDENT_ID = ?",
            arrayOf(studentId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                if (it.getInt(it.getColumnIndexOrThrow(KEY_COMPLETED)) == 1) {
                    completedCount++
                }
                val rating = it.getInt(it.getColumnIndexOrThrow(KEY_RATING))
                if (rating > 0) {
                    totalRating += rating
                    ratingCount++
                }
            }
        }

        val averageRating = if (ratingCount > 0) totalRating.toDouble() / ratingCount else 0.0
        return Pair(completedCount, averageRating)
    }

    fun getCompletionRate(): Double {
        val students = getStudents()
        if (students.isEmpty()) return 0.0

        val totalDays = getTotalDays()
        var totalCompleted = 0

        students.forEach { student ->
            val (completedCount, _) = getStudentStats(student.id)
            totalCompleted += completedCount
        }

        val expectedDuties = totalDays * students.size
        return if (expectedDuties > 0) totalCompleted.toDouble() / expectedDuties * 100 else 0.0
    }

    fun getAllDuties(): List<Duty> {
        val duties = mutableListOf<Duty>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DUTIES,
            null,
            null,
            null,
            null,
            null,
            "$KEY_ID ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                duties.add(
                    Duty(
                        id = it.getLong(it.getColumnIndexOrThrow(KEY_ID)),
                        studentId = it.getLong(it.getColumnIndexOrThrow(KEY_STUDENT_ID)),
                        date = it.getString(it.getColumnIndexOrThrow(KEY_DATE)),
                        type = it.getString(it.getColumnIndexOrThrow(KEY_TYPE)),
                        notes = it.getString(it.getColumnIndexOrThrow(KEY_NOTES))
                    )
                )
            }
        }
        return duties
    }

    fun getAllDutyRecords(): List<DutyRecord> {
        val records = mutableListOf<DutyRecord>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DUTY_RECORDS,
            null,
            null,
            null,
            null,
            null,
            "$KEY_ID ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                records.add(
                    DutyRecord(
                        id = it.getLong(it.getColumnIndexOrThrow(KEY_ID)),
                        studentId = it.getLong(it.getColumnIndexOrThrow(KEY_STUDENT_ID)),
                        date = it.getString(it.getColumnIndexOrThrow(KEY_DATE)),
                        completed = it.getInt(it.getColumnIndexOrThrow(KEY_COMPLETED)) == 1,
                        completedAt = it.getString(it.getColumnIndexOrThrow(KEY_COMPLETED_AT)),
                        rating = it.getInt(it.getColumnIndexOrThrow(KEY_RATING)),
                        reviewNotes = it.getString(it.getColumnIndexOrThrow(KEY_REVIEW_NOTES)),
                        likes = it.getInt(it.getColumnIndexOrThrow(KEY_LIKES))
                    )
                )
            }
        }
        return records
    }

    fun replaceAllDuties(duties: List<Duty>) {
        val db = writableDatabase
        db.delete(TABLE_DUTIES, null, null)
        duties.forEach { duty ->
            val values = ContentValues().apply {
                put(KEY_STUDENT_ID, duty.studentId)
                put(KEY_DATE, duty.date)
                put(KEY_TYPE, duty.type)
                put(KEY_NOTES, duty.notes)
            }
            db.insert(TABLE_DUTIES, null, values)
        }
    }

    fun replaceAllDutyRecords(records: List<DutyRecord>) {
        val db = writableDatabase
        db.delete(TABLE_DUTY_RECORDS, null, null)
        records.forEach { record ->
            val values = ContentValues().apply {
                put(KEY_STUDENT_ID, record.studentId)
                put(KEY_DATE, record.date)
                put(KEY_COMPLETED, if (record.completed) 1 else 0)
                put(KEY_COMPLETED_AT, record.completedAt)
                put(KEY_RATING, record.rating)
                put(KEY_REVIEW_NOTES, record.reviewNotes)
                put(KEY_LIKES, record.likes)
            }
            db.insert(TABLE_DUTY_RECORDS, null, values)
        }
    }

    fun addMemo(memo: Memo): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_STUDENT_ID, memo.studentId)
            put(KEY_TITLE, memo.title)
            put(KEY_CONTENT, memo.content)
            put(KEY_DATE, memo.date)
        }
        return db.insert(TABLE_MEMOS, null, values)
    }

    fun updateMemo(memo: Memo): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_STUDENT_ID, memo.studentId)
            put(KEY_TITLE, memo.title)
            put(KEY_CONTENT, memo.content)
            put(KEY_DATE, memo.date)
        }
        return db.update(TABLE_MEMOS, values, "$KEY_ID = ?", arrayOf(memo.id.toString()))
    }

    fun deleteMemo(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_MEMOS, "$KEY_ID = ?", arrayOf(id.toString()))
    }

    fun getMemos(): List<Memo> {
        val memos = mutableListOf<Memo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_MEMOS,
            null,
            null,
            null,
            null,
            null,
            "$KEY_DATE DESC, $KEY_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                memos.add(
                    Memo(
                        id = it.getLong(it.getColumnIndexOrThrow(KEY_ID)),
                        studentId = if (it.isNull(it.getColumnIndexOrThrow(KEY_STUDENT_ID))) null else it.getLong(it.getColumnIndexOrThrow(KEY_STUDENT_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(KEY_TITLE)),
                        content = it.getString(it.getColumnIndexOrThrow(KEY_CONTENT)),
                        date = it.getString(it.getColumnIndexOrThrow(KEY_DATE))
                    )
                )
            }
        }
        return memos
    }

    fun getMemosByStudent(studentId: Long): List<Memo> {
        val memos = mutableListOf<Memo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_MEMOS,
            null,
            "$KEY_STUDENT_ID = ?",
            arrayOf(studentId.toString()),
            null,
            null,
            "$KEY_DATE DESC, $KEY_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                memos.add(
                    Memo(
                        id = it.getLong(it.getColumnIndexOrThrow(KEY_ID)),
                        studentId = if (it.isNull(it.getColumnIndexOrThrow(KEY_STUDENT_ID))) null else it.getLong(it.getColumnIndexOrThrow(KEY_STUDENT_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(KEY_TITLE)),
                        content = it.getString(it.getColumnIndexOrThrow(KEY_CONTENT)),
                        date = it.getString(it.getColumnIndexOrThrow(KEY_DATE))
                    )
                )
            }
        }
        return memos
    }
}

data class DutyRecord(
    val id: Long,
    val studentId: Long,
    val date: String,
    val completed: Boolean,
    val completedAt: String?,
    val rating: Int,
    val reviewNotes: String?,
    val likes: Int
)

data class Memo(
    val id: Long,
    val studentId: Long?,
    val title: String,
    val content: String?,
    val date: String
)
