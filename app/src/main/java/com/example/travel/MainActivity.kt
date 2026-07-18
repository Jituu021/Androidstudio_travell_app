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
                    var currentUser by remember { mutableStateOf<User?>(null) }

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