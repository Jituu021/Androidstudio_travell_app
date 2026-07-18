package com.example.travel

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    dbHelper: TravelDatabaseHelper,
    onLoginSuccess: (User) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isEmailMode by remember { mutableStateOf(true) }
    var isLoginTab by remember { mutableStateOf(true) }

    // Email/Password states
    var emailInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }

    // Phone OTP states
    var otpPhoneInput by remember { mutableStateOf("") }
    var otpCodeInput by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("") }
    var showOtpField by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var otpTimer by remember { mutableStateOf(60) }

    // Google Sign-In Simulation states
    var showGoogleChooser by remember { mutableStateOf(false) }

    // Error messages
    var errorMessage by remember { mutableStateOf("") }

    // Gradient background
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F1B2B), // Dark Navy
            Color(0xFF070B12)  // Near Black
        )
    )

    // Timer effect for OTP
    LaunchedEffect(showOtpField, otpTimer) {
        if (showOtpField && otpTimer > 0) {
            delay(1000)
            otpTimer -= 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header / Logo Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TB",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "TRAVEL BUDDY",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "SECURE TELEMETRY ACCESS",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Auth Mode Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = {
                        isEmailMode = true
                        errorMessage = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEmailMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (isEmailMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("EMAIL / PASS", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        isEmailMode = false
                        errorMessage = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isEmailMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (!isEmailMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("PHONE + OTP", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Card body
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isEmailMode) {
                        // Login / Register Selector Tabs inside Email Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "LOGIN",
                                color = if (isLoginTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .clickable { isLoginTab = true; errorMessage = "" }
                                    .padding(vertical = 4.dp)
                            )
                            Text(
                                text = "REGISTER",
                                color = if (!isLoginTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .clickable { isLoginTab = false; errorMessage = "" }
                                    .padding(vertical = 4.dp)
                            )
                        }

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (isLoginTab) {
                            // Login Fields
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Username or Email") },
                                leadingIcon = { Text("👤", modifier = Modifier.padding(start = 8.dp)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password") },
                                leadingIcon = { Text("🔑", modifier = Modifier.padding(start = 8.dp)) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                                        errorMessage = "Fields cannot be empty."
                                        return@Button
                                    }
                                    val user = dbHelper.checkEmailLogin(emailInput.trim(), passwordInput.trim())
                                    if (user != null) {
                                        Toast.makeText(context, "Welcome back, ${user.username}!", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess(user)
                                    } else {
                                        errorMessage = "Invalid credentials. Try again."
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("ACCESS PROTOCOL", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Register Fields
                            OutlinedTextField(
                                value = usernameInput,
                                onValueChange = { usernameInput = it },
                                label = { Text("Username") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Phone Number") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password (min 6 chars)") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = confirmPasswordInput,
                                onValueChange = { confirmPasswordInput = it },
                                label = { Text("Confirm Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (usernameInput.trim().isEmpty() || emailInput.trim().isEmpty() || phoneInput.trim().isEmpty() || passwordInput.isEmpty()) {
                                        errorMessage = "All fields are required."
                                        return@Button
                                    }
                                    if (passwordInput.length < 6) {
                                        errorMessage = "Password must be at least 6 characters."
                                        return@Button
                                    }
                                    if (passwordInput != confirmPasswordInput) {
                                        errorMessage = "Passwords do not match."
                                        return@Button
                                    }
                                    val success = dbHelper.registerUser(
                                        usernameInput.trim(),
                                        emailInput.trim(),
                                        phoneInput.trim(),
                                        passwordInput.trim()
                                    )
                                    if (success) {
                                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                        val loggedIn = dbHelper.checkEmailLogin(emailInput.trim(), passwordInput.trim())
                                        if (loggedIn != null) onLoginSuccess(loggedIn)
                                    } else {
                                        errorMessage = "Error: Username/Email already exists."
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("REGISTER NEW PROFILE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Phone OTP Module
                        Text(
                            text = "OTP VERIFICATION PORTAL",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (!showOtpField) {
                            OutlinedTextField(
                                value = otpPhoneInput,
                                onValueChange = { otpPhoneInput = it },
                                label = { Text("Phone Number (+91)") },
                                leadingIcon = { Text("🇮🇳", modifier = Modifier.padding(start = 8.dp)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    val trimmed = otpPhoneInput.trim()
                                    if (trimmed.length != 10 || !trimmed.all { it.isDigit() }) {
                                        errorMessage = "Enter a valid 10-digit Indian phone number."
                                        return@Button
                                    }
                                    coroutineScope.launch {
                                        isSendingOtp = true
                                        delay(1500)
                                        isSendingOtp = false
                                        val randOtp = (100000..999999).random().toString()
                                        generatedOtp = randOtp
                                        showOtpField = true
                                        otpTimer = 60
                                        errorMessage = ""
                                        // Show Dialog/Toast simulator
                                        Toast.makeText(context, "SYSTEM: OTP sent to +91 $trimmed.\nUse Code: $randOtp", Toast.LENGTH_LONG).show()
                                    }
                                },
                                enabled = !isSendingOtp,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isSendingOtp) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("SEND VERIFICATION OTP", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Text(
                                text = "Verification code sent to +91 $otpPhoneInput. Enter below:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = otpCodeInput,
                                onValueChange = { otpCodeInput = it },
                                label = { Text("6-Digit OTP") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (otpTimer > 0) {
                                    Text(
                                        text = "Resend OTP in ${otpTimer}s",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = FontFamily.Monospace
                                    )
                                } else {
                                    Text(
                                        text = "Resend OTP",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.clickable {
                                            val randOtp = (100000..999999).random().toString()
                                            generatedOtp = randOtp
                                            otpTimer = 60
                                            Toast.makeText(context, "SYSTEM: OTP Resent.\nUse Code: $randOtp", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }

                                Text(
                                    text = "Change Number",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable {
                                        showOtpField = false
                                        otpCodeInput = ""
                                        errorMessage = ""
                                    }
                                )
                            }

                            Button(
                                onClick = {
                                    if (otpCodeInput.trim() == generatedOtp) {
                                        val userPhone = "+91$otpPhoneInput"
                                        // Retrieve or register automatically in database helper
                                        val existingUser = dbHelper.checkPhoneLogin(userPhone)
                                        if (existingUser != null) {
                                            Toast.makeText(context, "Welcome back, ${existingUser.username}!", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess(existingUser)
                                        } else {
                                            // Auto-register
                                            val autoUser = "user_" + (1000..9999).random().toString()
                                            val autoEmail = "$autoUser@travelbuddy.in"
                                            dbHelper.registerUser(autoUser, autoEmail, userPhone, "PhoneAuth123")
                                            val newlyCreated = dbHelper.checkPhoneLogin(userPhone)
                                            if (newlyCreated != null) {
                                                Toast.makeText(context, "Profile registered successfully via OTP!", Toast.LENGTH_SHORT).show()
                                                onLoginSuccess(newlyCreated)
                                            }
                                        }
                                    } else {
                                        errorMessage = "Verification Failed: Incorrect 6-digit OTP code."
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("VERIFY & LOG IN", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Google Authentication Section
            Text(
                text = "— OR REGISTER VIA THIRD-PARTY SECURITY —",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            // Google Button
            Button(
                onClick = { showGoogleChooser = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "G",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFEA4335), // Google Red
                        fontSize = 18.sp
                    )
                    Text(
                        text = "SIGN IN WITH GOOGLE ACCOUNT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            // Bypass Button for Testing
            TextButton(
                onClick = {
                    val adminUser = dbHelper.checkEmailLogin("admin", "AdminPassword123")
                    if (adminUser != null) {
                        Toast.makeText(context, "System Admin Override Active", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(adminUser)
                    }
                }
            ) {
                Text(
                    text = "[ BYPASS MATRIX: LOAD DEFAULT ADMIN SESSION ]",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Google Accounts Dialog Simulator
        if (showGoogleChooser) {
            AlertDialog(
                onDismissRequest = { showGoogleChooser = false },
                title = {
                    Text(
                        text = "Choose an account",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GoogleAccountRow(name = "Jitendra Kumar", email = "jitendra021@gmail.com") {
                            showGoogleChooser = false
                            val user = dbHelper.checkOrCreateGoogleUser("jitendra021@gmail.com", "jitendra_kumar")
                            Toast.makeText(context, "Authenticated as jitendra_kumar", Toast.LENGTH_SHORT).show()
                            onLoginSuccess(user)
                        }
                        GoogleAccountRow(name = "Himalayan Explorer", email = "exp.himalaya@gmail.com") {
                            showGoogleChooser = false
                            val user = dbHelper.checkOrCreateGoogleUser("exp.himalaya@gmail.com", "himalayan_exp")
                            Toast.makeText(context, "Authenticated as himalayan_exp", Toast.LENGTH_SHORT).show()
                            onLoginSuccess(user)
                        }
                        GoogleAccountRow(name = "Guest User", email = "guest.travelbuddy@gmail.com") {
                            showGoogleChooser = false
                            val user = dbHelper.checkOrCreateGoogleUser("guest.travelbuddy@gmail.com", "guest_user")
                            Toast.makeText(context, "Authenticated as guest_user", Toast.LENGTH_SHORT).show()
                            onLoginSuccess(user)
                        }
                    }
                },
                containerColor = Color.White,
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showGoogleChooser = false }) {
                        Text("CANCEL", color = Color.Gray, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }
    }
}

@Composable
fun GoogleAccountRow(name: String, email: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1), fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
        Column {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
            Text(email, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
