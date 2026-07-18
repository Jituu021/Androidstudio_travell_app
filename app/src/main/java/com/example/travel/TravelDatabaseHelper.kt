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
    val isAdmin: Boolean
)

class TravelDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "travel_buddy.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_IS_ADMIN = "is_admin"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PHONE TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_IS_ADMIN INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Seed default admin account
        val adminValues = ContentValues().apply {
            put(COLUMN_USERNAME, "admin")
            put(COLUMN_EMAIL, "admin@travelbuddy.com")
            put(COLUMN_PHONE, "+919999999999")
            put(COLUMN_PASSWORD, "AdminPassword123")
            put(COLUMN_IS_ADMIN, 1)
        }
        db.insert(TABLE_USERS, null, adminValues)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registerUser(username: String, email: String, phone: String, password: String, isAdmin: Boolean = false): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_PASSWORD, password)
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
            val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            user = User(id, username, email, phone, isAdmin)
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
            val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            user = User(id, username, email, ph, isAdmin)
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
            val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            cursor.close()
            return User(id, dbUsername, email, phone, isAdmin)
        }
        cursor.close()

        // Create new user automatically
        val dummyPhone = "+91" + (9000000000L + (Math.random() * 999999999L).toLong()).toString()
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, dummyPhone)
            put(COLUMN_PASSWORD, "GoogleAuth123")
            put(COLUMN_IS_ADMIN, 0)
        }
        val newId = db.insert(TABLE_USERS, null, values)
        return User(newId.toInt(), username, email, dummyPhone, false)
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
                val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
                list.add(User(id, username, email, phone, isAdmin))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun clearDatabase(): Boolean {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_USERS WHERE $COLUMN_USERNAME != 'admin'")
        return true
    }
}
