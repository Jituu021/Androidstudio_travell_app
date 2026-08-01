package com.example.travel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.travel.ui.theme.TravelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbHelper = TravelDatabaseHelper(this)
        setContent {
            TravelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val defaultAdmin = remember {
                        dbHelper.checkEmailLogin("admin@travelbuddy.com", "AdminPassword123")
                            ?: run {
                                dbHelper.registerUser("System Admin", "admin@travelbuddy.com", "+919999999999", "AdminPassword123", "Mumbai, India", "Frequently", true)
                                dbHelper.checkEmailLogin("admin@travelbuddy.com", "AdminPassword123")
                            }
                    }
                    var currentUser by remember { mutableStateOf<User?>(defaultAdmin) }

                    if (currentUser == null) {
                        LoginScreen(dbHelper = dbHelper, onLoginSuccess = { user ->
                            currentUser = user
                        })
                    } else {
                        NexusGuideScreen(
                            dbHelper = dbHelper,
                            userSession = currentUser!!,
                            onLogOut = { currentUser = null }
                        )
                    }
                }
            }
        }
    }
}