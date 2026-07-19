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

    // Main Mode: true = OTP mode, false = Traditional (Email/Password & Google)
    var isOtpPortalMode by remember { mutableStateOf(true) }
    // Tab within OTP mode: true = LOGIN, false = REGISTER
    var isOtpLoginTab by remember { mutableStateOf(true) }
    
    // Tab within Traditional mode: true = LOGIN, false = REGISTER
    var isTradLoginTab by remember { mutableStateOf(true) }

    // Common Inputs
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var homeLocationInput by remember { mutableStateOf("") }
    var travelFrequencyInput by remember { mutableStateOf("") }

    // Traditional Password inputs
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }

    // OTP verification inputs
    var otpCodeInput by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("") }
    var showOtpField by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var otpTimer by remember { mutableStateOf(60) }

    // Google Sign-In Simulation states
    var showGoogleChooser by remember { mutableStateOf(false) }

    // Error / Status Message
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    // Gradient background
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F1B2F), // Sleek Dark Navy
            Color(0xFF080D18)  // Near Black
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
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TB",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "TRAVEL BUDDY",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "OFFLINE-FIRST TRAVEL COMPANION",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Top level selector between OTP portal & password portal
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
                        isOtpPortalMode = true
                        errorMessage = ""
                        successMessage = ""
                        showOtpField = false
                        otpCodeInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOtpPortalMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (isOtpPortalMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("OTP ACCESS", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        isOtpPortalMode = false
                        errorMessage = ""
                        successMessage = ""
                        showOtpField = false
                        otpCodeInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isOtpPortalMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (!isOtpPortalMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("PASSWORDS", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Main Card container for form
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
                    // Portal Specific Header Tabs (LOGIN vs REGISTER)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        val isLogin = if (isOtpPortalMode) isOtpLoginTab else isTradLoginTab
                        Text(
                            text = "LOG IN",
                            color = if (isLogin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clickable {
                                    if (isOtpPortalMode) isOtpLoginTab = true else isTradLoginTab = true
                                    errorMessage = ""
                                    successMessage = ""
                                    showOtpField = false
                                    otpCodeInput = ""
                                }
                                .padding(vertical = 4.dp)
                        )
                        Text(
                            text = "REGISTER",
                            color = if (!isLogin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clickable {
                                    if (isOtpPortalMode) isOtpLoginTab = false else isTradLoginTab = false
                                    errorMessage = ""
                                    successMessage = ""
                                    showOtpField = false
                                    otpCodeInput = ""
                                }
                                .padding(vertical = 4.dp)
                        )
                    }

                    // Dynamic error / success banners
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = "⚠️ $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (successMessage.isNotEmpty()) {
                        Text(
                            text = "✓ $successMessage",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // RENDER FORMS
                    if (isOtpPortalMode) {
                        // ==========================================
                        // OTP PORTAL MODE (LOGIN / REGISTER)
                        // ==========================================
                        if (isOtpLoginTab) {
                            // OTP LOGIN TAB
                            if (!showOtpField) {
                                Text(
                                    text = "Enter your registered mobile number to log in via one-time passkey.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it },
                                    label = { Text("Phone Number (+91)") },
                                    leadingIcon = { Text("🇮🇳", modifier = Modifier.padding(start = 8.dp)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Button(
                                    onClick = {
                                        val trimmed = phoneInput.trim()
                                        if (trimmed.length != 10 || !trimmed.all { it.isDigit() }) {
                                            errorMessage = "Enter a valid 10-digit phone number."
                                            return@Button
                                        }
                                        
                                        val existing = dbHelper.checkPhoneLogin("+91$trimmed")
                                        if (existing == null) {
                                            errorMessage = "Phone number is not registered. Please switch to the REGISTER tab."
                                            return@Button
                                        }

                                        coroutineScope.launch {
                                            isSendingOtp = true
                                            errorMessage = ""
                                            delay(1500)
                                            isSendingOtp = false
                                            val code = (100000..999999).random().toString()
                                            generatedOtp = code
                                            showOtpField = true
                                            otpTimer = 60
                                            successMessage = "OTP sent to +91 $trimmed"
                                            Toast.makeText(context, "[SMS GATEWAY SIMULATION]\nFrom: Travel Buddy Verification\nCode: $code\n(Use this code to log in)", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    enabled = !isSendingOtp,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    if (isSendingOtp) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("SEND LOG IN OTP", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // Show OTP verify field
                                Text(
                                    text = "Simulated login code sent to +91 $phoneInput. Enter it below:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = otpCodeInput,
                                    onValueChange = { otpCodeInput = it },
                                    label = { Text("6-Digit Login Code") },
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
                                            text = "Resend in ${otpTimer}s",
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
                                                val code = (100000..999999).random().toString()
                                                generatedOtp = code
                                                otpTimer = 60
                                                successMessage = "New OTP sent."
                                                Toast.makeText(context, "[SMS GATEWAY SIMULATION]\nFrom: Travel Buddy Verification\nCode: $code", Toast.LENGTH_LONG).show()
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
                                            successMessage = ""
                                        }
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (otpCodeInput.trim() == generatedOtp) {
                                            val user = dbHelper.checkPhoneLogin("+91${phoneInput.trim()}")
                                            if (user != null) {
                                                Toast.makeText(context, "Log in successful!", Toast.LENGTH_SHORT).show()
                                                onLoginSuccess(user)
                                            } else {
                                                errorMessage = "Error logging in. Try registering instead."
                                            }
                                        } else {
                                            errorMessage = "Invalid verification code."
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("VERIFY & LOG IN", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // OTP REGISTER TAB
                            if (!showOtpField) {
                                Text(
                                    text = "Complete your traveler profile below to register using OTP authentication.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Name
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = { Text("Name") },
                                    leadingIcon = { Text("👤", modifier = Modifier.padding(start = 8.dp)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                // Mail ID
                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { emailInput = it },
                                    label = { Text("Mail ID") },
                                    leadingIcon = { Text("✉️", modifier = Modifier.padding(start = 8.dp)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                // Phone Number
                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it },
                                    label = { Text("Phone Number") },
                                    leadingIcon = { Text("📞", modifier = Modifier.padding(start = 8.dp)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                // Your Home Location
                                OutlinedTextField(
                                    value = homeLocationInput,
                                    onValueChange = { homeLocationInput = it },
                                    label = { Text("Your Home Location") },
                                    leadingIcon = { Text("📍", modifier = Modifier.padding(start = 8.dp)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                // How often you travel
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedTextField(
                                        value = travelFrequencyInput,
                                        onValueChange = { travelFrequencyInput = it },
                                        label = { Text("How often you travel") },
                                        leadingIcon = { Text("✈️", modifier = Modifier.padding(start = 8.dp)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    
                                    // Quick Choice Chips
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val chips = listOf("Weekly", "Monthly", "Occasionally", "Rarely")
                                        chips.forEach { choice ->
                                            Box(
                                                modifier = Modifier
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (travelFrequencyInput == choice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                                        shape = RoundedCornerShape(20.dp)
                                                    )
                                                    .background(
                                                        if (travelFrequencyInput == choice) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                                        RoundedCornerShape(20.dp)
                                                    )
                                                    .clickable { travelFrequencyInput = choice }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text(text = choice, fontSize = 10.sp, color = if (travelFrequencyInput == choice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (nameInput.trim().isEmpty() || emailInput.trim().isEmpty() || phoneInput.trim().isEmpty() || homeLocationInput.trim().isEmpty() || travelFrequencyInput.trim().isEmpty()) {
                                            errorMessage = "All registration fields are required."
                                            return@Button
                                        }
                                        val trimmedPhone = phoneInput.trim()
                                        if (trimmedPhone.length != 10 || !trimmedPhone.all { it.isDigit() }) {
                                            errorMessage = "Enter a valid 10-digit phone number."
                                            return@Button
                                        }

                                        coroutineScope.launch {
                                            isSendingOtp = true
                                            errorMessage = ""
                                            delay(1500)
                                            isSendingOtp = false
                                            val code = (100000..999999).random().toString()
                                            generatedOtp = code
                                            showOtpField = true
                                            otpTimer = 60
                                            successMessage = "OTP sent to email & phone."
                                            Toast.makeText(context, "[OTP REGISTRATION SYSTEM]\nVerification code sent to $emailInput and +91 $trimmedPhone.\n\nUse Code: $code", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    enabled = !isSendingOtp,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    if (isSendingOtp) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("GENERATE REGISTRATION OTP", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            } else {
                                // Show Register OTP field
                                Text(
                                    text = "Enter the 6-digit registration OTP code sent to $emailInput:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = otpCodeInput,
                                    onValueChange = { otpCodeInput = it },
                                    label = { Text("6-Digit Verification Code") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Button(
                                    onClick = {
                                        if (otpCodeInput.trim() == generatedOtp) {
                                            val fullPhone = "+91${phoneInput.trim()}"
                                            val success = dbHelper.registerUser(
                                                username = nameInput.trim(),
                                                email = emailInput.trim(),
                                                phone = fullPhone,
                                                password = "OtpAuth123",
                                                homeLocation = homeLocationInput.trim(),
                                                travelFrequency = travelFrequencyInput.trim()
                                            )
                                            if (success) {
                                                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                                val loggedIn = dbHelper.checkPhoneLogin(fullPhone)
                                                if (loggedIn != null) onLoginSuccess(loggedIn)
                                            } else {
                                                errorMessage = "Error registering profile: Mail ID or Phone Number already exists."
                                                showOtpField = false
                                            }
                                        } else {
                                            errorMessage = "Incorrect OTP code. Try again."
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("VERIFY & COMPLETE REGISTER", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        // ==========================================
                        // TRADITIONAL PORTAL MODE
                        // ==========================================
                        if (isTradLoginTab) {
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
                                Text("ACCESS CONSOLE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // REGISTER PASSWORD TAB
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Mail ID") },
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
                                value = homeLocationInput,
                                onValueChange = { homeLocationInput = it },
                                label = { Text("Home Location") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = travelFrequencyInput,
                                onValueChange = { travelFrequencyInput = it },
                                label = { Text("How often you travel") },
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
                                    if (nameInput.trim().isEmpty() || emailInput.trim().isEmpty() || phoneInput.trim().isEmpty() || passwordInput.isEmpty()) {
                                        errorMessage = "Fields marked with Name, Mail ID, Phone, Password are required."
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
                                        username = nameInput.trim(),
                                        email = emailInput.trim(),
                                        phone = "+91${phoneInput.trim()}",
                                        password = passwordInput.trim(),
                                        homeLocation = homeLocationInput.trim(),
                                        travelFrequency = travelFrequencyInput.trim()
                                    )
                                    if (success) {
                                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                        val loggedIn = dbHelper.checkEmailLogin(emailInput.trim(), passwordInput.trim())
                                        if (loggedIn != null) onLoginSuccess(loggedIn)
                                    } else {
                                        errorMessage = "Error: Email or Phone already exists."
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("REGISTER ACCOUNT", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Google Authentication Section
            Text(
                text = "— OR QUICK THIRD-PARTY ACCESS —",
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
                    val adminUser = dbHelper.checkEmailLogin("Admin", "AdminPassword123")
                    if (adminUser != null) {
                        Toast.makeText(context, "System Admin Override Active", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(adminUser)
                    } else {
                        // Fallback register admin if database reset
                        dbHelper.registerUser("Admin", "admin@travelbuddy.com", "+919999999999", "AdminPassword123", "New Delhi, India", "Frequently", true)
                        val adminRetry = dbHelper.checkEmailLogin("Admin", "AdminPassword123")
                        if (adminRetry != null) {
                            Toast.makeText(context, "System Admin Override Restored", Toast.LENGTH_SHORT).show()
                            onLoginSuccess(adminRetry)
                        }
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
