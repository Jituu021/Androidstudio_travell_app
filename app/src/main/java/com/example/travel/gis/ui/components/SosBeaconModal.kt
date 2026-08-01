package com.example.travel.gis.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travel.gis.domain.model.LocationTelemetry
import java.util.Locale

@Composable
fun SosBeaconModal(
    telemetry: LocationTelemetry,
    address: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var sosPhoneInput by remember { mutableStateOf("+919999999999") }

    val latFormatted = String.format(Locale.US, "%.5f", telemetry.latitude)
    val lonFormatted = String.format(Locale.US, "%.5f", telemetry.longitude)
    val mapsUrl = "https://maps.google.com/?q=$latFormatted,$lonFormatted"
    val smsBody = "EMERGENCY SOS ALERT! I need help at: $address (GPS: $latFormatted°, $lonFormatted°). Map Link: $mapsUrl"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚨", fontSize = 14.sp)
                }
                Text(
                    text = "EMERGENCY SOS BEACON",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Red
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // High-Accuracy Telemetry Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("📍 REAL-TIME GPS TELEMETRY", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                        Text("Location: $address", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("Coordinates: $latFormatted°, $lonFormatted°", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("Altitude: ${telemetry.altitudeMeters.toInt()}m • Speed: ${telemetry.speedKmH.toInt()} km/h", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Phone Input for Emergency Contact
                OutlinedTextField(
                    value = sosPhoneInput,
                    onValueChange = { sosPhoneInput = it },
                    label = { Text("Emergency Contact Phone", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Hotline Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("CALL 112 POLICE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:102"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("CALL 102 AMBULANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val uri = Uri.parse("smsto:$sosPhoneInput")
                    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                        putExtra("sms_body", smsBody)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("SEND SOS SMS BROADCAST 📩", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", fontWeight = FontWeight.Bold)
            }
        }
    )
}
