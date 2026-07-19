package com.example.travel

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val phone: String,
    val homeLocation: String,
    val travelFrequency: String,
    val isAdmin: Boolean
)

data class TripNote(
    val id: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val timestamp: String
)

data class TravelExpense(
    val id: Int,
    val userId: Int,
    val amount: Double,
    val category: String, // Food, Fuel, Hotel, Shopping
    val description: String,
    val timestamp: String
)

class TravelDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "travel_buddy.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_HOME_LOCATION = "home_location"
        const val COLUMN_TRAVEL_FREQUENCY = "travel_frequency"
        const val COLUMN_IS_ADMIN = "is_admin"

        // Trip Notes
        const val TABLE_NOTES = "trip_notes"
        const val COLUMN_NOTE_ID = "id"
        const val COLUMN_NOTE_USER_ID = "user_id"
        const val COLUMN_NOTE_TITLE = "title"
        const val COLUMN_NOTE_CONTENT = "content"
        const val COLUMN_NOTE_TIMESTAMP = "timestamp"

        // Travel Expenses
        const val TABLE_EXPENSES = "travel_expenses"
        const val COLUMN_EXPENSE_ID = "id"
        const val COLUMN_EXPENSE_USER_ID = "user_id"
        const val COLUMN_EXPENSE_AMOUNT = "amount"
        const val COLUMN_EXPENSE_CATEGORY = "category"
        const val COLUMN_EXPENSE_DESC = "description"
        const val COLUMN_EXPENSE_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PHONE TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_HOME_LOCATION TEXT,
                $COLUMN_TRAVEL_FREQUENCY TEXT,
                $COLUMN_IS_ADMIN INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        val createNotesQuery = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTE_USER_ID INTEGER,
                $COLUMN_NOTE_TITLE TEXT,
                $COLUMN_NOTE_CONTENT TEXT,
                $COLUMN_NOTE_TIMESTAMP TEXT
            )
        """.trimIndent()
        db.execSQL(createNotesQuery)

        val createExpensesQuery = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXPENSE_USER_ID INTEGER,
                $COLUMN_EXPENSE_AMOUNT REAL,
                $COLUMN_EXPENSE_CATEGORY TEXT,
                $COLUMN_EXPENSE_DESC TEXT,
                $COLUMN_EXPENSE_TIMESTAMP TEXT
            )
        """.trimIndent()
        db.execSQL(createExpensesQuery)

        // Seed default admin account
        val adminValues = ContentValues().apply {
            put(COLUMN_USERNAME, "Admin")
            put(COLUMN_EMAIL, "admin@travelbuddy.com")
            put(COLUMN_PHONE, "+919999999999")
            put(COLUMN_PASSWORD, "AdminPassword123")
            put(COLUMN_HOME_LOCATION, "New Delhi, India")
            put(COLUMN_TRAVEL_FREQUENCY, "Frequently")
            put(COLUMN_IS_ADMIN, 1)
        }
        db.insert(TABLE_USERS, null, adminValues)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun registerUser(
        username: String,
        email: String,
        phone: String,
        password: String = "OtpAuth123",
        homeLocation: String = "",
        travelFrequency: String = "",
        isAdmin: Boolean = false
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_HOME_LOCATION, homeLocation)
            put(COLUMN_TRAVEL_FREQUENCY, travelFrequency)
            put(COLUMN_IS_ADMIN, if (isAdmin) 1 else 0)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun checkEmailLogin(emailOrUsername: String, password: String): User? {
        val db = this.readableDatabase
        val query = """
            SELECT * FROM $TABLE_USERS 
            WHERE ($COLUMN_EMAIL = ? OR $COLUMN_USERNAME = ?) AND $COLUMN_PASSWORD = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(emailOrUsername, emailOrUsername, password))
        
        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
            val home = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOME_LOCATION)) ?: ""
            val freq = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRAVEL_FREQUENCY)) ?: ""
            val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            user = User(id, username, email, phone, home, freq, isAdmin)
        }
        cursor.close()
        return user
    }

    fun checkPhoneLogin(phone: String): User? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_PHONE = ?"
        val cursor = db.rawQuery(query, arrayOf(phone))
        
        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val ph = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
            val home = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOME_LOCATION)) ?: ""
            val freq = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRAVEL_FREQUENCY)) ?: ""
            val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            user = User(id, username, email, ph, home, freq, isAdmin)
        }
        cursor.close()
        return user
    }

    fun checkOrCreateGoogleUser(email: String, username: String): User {
        val db = this.writableDatabase
        // check if user exists
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val dbUsername = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)) ?: ""
            val home = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOME_LOCATION)) ?: ""
            val freq = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRAVEL_FREQUENCY)) ?: ""
            val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            cursor.close()
            return User(id, dbUsername, email, phone, home, freq, isAdmin)
        }
        cursor.close()

        // Create new user automatically
        val dummyPhone = "+91" + (9000000000L + (Math.random() * 999999999L).toLong()).toString()
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, dummyPhone)
            put(COLUMN_PASSWORD, "GoogleAuth123")
            put(COLUMN_HOME_LOCATION, "New Delhi, India")
            put(COLUMN_TRAVEL_FREQUENCY, "Occasionally")
            put(COLUMN_IS_ADMIN, 0)
        }
        val newId = db.insert(TABLE_USERS, null, values)
        return User(newId.toInt(), username, email, dummyPhone, "New Delhi, India", "Occasionally", false)
    }

    fun getAllUsers(): List<User> {
        val list = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)) ?: ""
                val home = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOME_LOCATION)) ?: ""
                val freq = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRAVEL_FREQUENCY)) ?: ""
                val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
                list.add(User(id, username, email, phone, home, freq, isAdmin))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- TRIP NOTES METHODS ---
    fun addTripNote(userId: Int, title: String, content: String, timestamp: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_USER_ID, userId)
            put(COLUMN_NOTE_TITLE, title)
            put(COLUMN_NOTE_CONTENT, content)
            put(COLUMN_NOTE_TIMESTAMP, timestamp)
        }
        val result = db.insert(TABLE_NOTES, null, values)
        return result != -1L
    }

    fun getTripNotes(userId: Int): List<TripNote> {
        val list = mutableListOf<TripNote>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NOTES WHERE $COLUMN_NOTE_USER_ID = ? ORDER BY $COLUMN_NOTE_ID DESC",
            arrayOf(userId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TIMESTAMP))
                list.add(TripNote(id, userId, title, content, time))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun deleteTripNote(noteId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NOTES, "$COLUMN_NOTE_ID = ?", arrayOf(noteId.toString()))
        return result > 0
    }

    // --- EXPENSES METHODS ---
    fun addExpense(userId: Int, amount: Double, category: String, description: String, timestamp: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EXPENSE_USER_ID, userId)
            put(COLUMN_EXPENSE_AMOUNT, amount)
            put(COLUMN_EXPENSE_CATEGORY, category)
            put(COLUMN_EXPENSE_DESC, description)
            put(COLUMN_EXPENSE_TIMESTAMP, timestamp)
        }
        val result = db.insert(TABLE_EXPENSES, null, values)
        return result != -1L
    }

    fun getExpenses(userId: Int): List<TravelExpense> {
        val list = mutableListOf<TravelExpense>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_EXPENSE_USER_ID = ? ORDER BY $COLUMN_EXPENSE_ID DESC",
            arrayOf(userId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_AMOUNT))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_CATEGORY))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DESC))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_TIMESTAMP))
                list.add(TravelExpense(id, userId, amount, category, desc, time))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun deleteExpense(expenseId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_EXPENSES, "$COLUMN_EXPENSE_ID = ?", arrayOf(expenseId.toString()))
        return result > 0
    }

    fun clearDatabase(): Boolean {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_USERS WHERE $COLUMN_USERNAME != 'Admin'")
        db.execSQL("DELETE FROM $TABLE_NOTES")
        db.execSQL("DELETE FROM $TABLE_EXPENSES")
        return true
    }
}
