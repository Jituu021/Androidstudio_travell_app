import os

file_path = r"c:\Users\Administrator\Desktop\Androidstudio_travell_app\app\src\main\java\com\example\travel\NexusGuideScreen.kt"

if not os.path.exists(file_path):
    print(f"Error: {file_path} not found")
    exit(1)

with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Define the new MapsScreen block
new_maps_screen = """fun MapsScreen() {
    var elevation by remember { mutableStateOf(2450) }
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var newReviewText by remember { mutableStateOf("") }
    var newReviewAuthor by remember { mutableStateOf("EXPLORER_01") }

    // Simulated elevation sensor drift (+/- 1M every 5s)
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            val drift = (-1..1).random()
            elevation = (elevation + drift).coerceIn(2445, 2455)
        }
    }

    // Mutable list of log records to show dynamic review adding
    val logsList = remember {
        mutableStateListOf(
            LogEntry(
                initials = "EX_42",
                name = "EXPLORER_ALFA",
                timeAgo = "3 HOURS AGO",
                distance = "12M AWAY",
                isNearest = true,
                isAlfa = true,
                comment = "Water source is currently frozen. Shelter structure remains 100% intact. High winds recorded at night.",
                photos = listOf(R.drawable.frozen_pump, R.drawable.shelter_interior)
            ),
            LogEntry(
                initials = "TR_09",
                name = "TRAIL_WATCHER",
                timeAgo = "1 DAY AGO",
                distance = "45M AWAY",
                isNearest = false,
                isAlfa = false,
                comment = "Perfect waypoint. Signal is strongest on the north-facing balcony. Log book is full, needs replacement."
            ),
            LogEntry(
                initials = "GT_88",
                name = "GUIDE_THOMAS",
                timeAgo = "3 DAYS AGO",
                distance = "8M AWAY",
                isNearest = false,
                isAlfa = false,
                comment = "Solid outpost. The solar array is clean and charging well. Highly recommend for a multi-day hub.",
                opacity = 0.8f
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero / Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.alpine_outpost),
                    contentDescription = "Alpine Outpost Scenery",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 180f
                            )
                        )
                )

                // Outpost metadata overlay bottom-left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "ALPINE OUTPOST",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Rating stars (4.5 rating)
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            StarIcon(filled = true)
                            StarIcon(filled = true)
                            StarIcon(filled = true)
                            StarIcon(filled = true)
                            HalfStarIcon()
                        }
                        Text(
                            text = "4.8 (124 VERIFIED)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Proximity Lock Verification Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                // Background watermarked icon decoration
                ShieldIcon(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            ProximityLockIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "PROXIMITY_LOCK: UNLOCKED",
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "You are within 50m - Review Access Granted",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Action Button
                    Button(
                        onClick = { showAddReviewDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RateReviewIcon(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "ADD VERIFIED REVIEW",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Stats Bento Grid (Elevation / Connectivity)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Elevation
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "ELEVATION",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format(Locale.US, "%,dM", elevation),
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Connectivity
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CONNECTIVITY",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "STABLE_VHF",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Verified logs header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VERIFIED_LOGS",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "FILTER: DISTANCE_NEAREST",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Logs / Reviews list Column
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                logsList.forEach { log ->
                    LogCard(log = log)
                }
            }

            // Distance Visualizer (Atmospheric)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.66f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "0M", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
                    Text(text = "50M_LIMIT", color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(text = "500M", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
                }
            }
        }

        // Review Composer Dialog
        if (showAddReviewDialog) {
            AlertDialog(
                onDismissRequest = { showAddReviewDialog = false },
                title = {
                    Text(
                        text = "ADD VERIFIED EXPLORER LOG",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Outpost coordinates synced. Type your transmission below:",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = newReviewText,
                            onValueChange = { newReviewText = it },
                            label = { Text("Log Entry Content") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        OutlinedTextField(
                            value = newReviewAuthor,
                            onValueChange = { newReviewAuthor = it },
                            label = { Text("Callsign / Handle") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                confirmButton = {
                    Button(
                        onClick = {
                            if (newReviewText.isNotEmpty()) {
                                logsList.add(
                                    0,
                                    LogEntry(
                                        initials = if (newReviewAuthor.length >= 2) newReviewAuthor.take(2).uppercase() else "EX",
                                        name = newReviewAuthor.uppercase(),
                                        timeAgo = "JUST NOW",
                                        distance = "0M AWAY",
                                        isNearest = true,
                                        isAlfa = false,
                                        comment = newReviewText
                                    )
                                )
                                newReviewText = ""
                                showAddReviewDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "SUBMIT LOG MATRIX",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddReviewDialog = false }) {
                        Text(
                            text = "ABORT",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            )
        }
    }
}

// Data class represent logs
data class LogEntry(
    val initials: String,
    val name: String,
    val timeAgo: String,
    val distance: String,
    val isNearest: Boolean,
    val isAlfa: Boolean,
    val comment: String,
    val photos: List<Int> = emptyList(),
    val opacity: Float = 1.0f
)"""

start_marker = "fun MapsScreen() {"
end_marker = "fun TranslateScreen() {"

start_idx = content.find(start_marker)
end_idx = content.find(end_marker)

if start_idx == -1:
    print("Could not find MapsScreen start marker")
    exit(1)
if end_idx == -1:
    print("Could not find TranslateScreen start marker")
    exit(1)

# Keep the @Composable tag before fun MapsScreen()
# Usually @Composable is a few lines above. Let's find it.
# Actually, start_idx points to 'fun MapsScreen() {'.
# We want to replace from start_idx to end_idx, but insert new_maps_screen and a separator, keeping @Composable fun TranslateScreen() { intact.
new_block = new_maps_screen + "\n\n// ----------------------------------------------------\n// SCREEN 3: SCI-FI TRANSLATE SCREEN\n// ----------------------------------------------------\n@Composable\n"

content = content[:start_idx] + new_block + content[end_idx:]

# Define new icons to be appended at the end of the file
new_icons = """

@Composable
fun MoreVertIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val r = 2.dp.toPx()
        drawCircle(color = color, radius = r, center = Offset(w * 0.5f, h * 0.25f))
        drawCircle(color = color, radius = r, center = Offset(w * 0.5f, h * 0.5f))
        drawCircle(color = color, radius = r, center = Offset(w * 0.5f, h * 0.75f))
    }
}

@Composable
fun ProximityLockIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.35f,
            style = Stroke(width = stroke)
        )
        drawLine(color = color, start = Offset(w * 0.5f, 0f), end = Offset(w * 0.5f, h * 0.2f), strokeWidth = stroke)
        drawLine(color = color, start = Offset(w * 0.5f, h * 0.8f), end = Offset(w * 0.5f, h), strokeWidth = stroke)
        drawLine(color = color, start = Offset(0f, h * 0.5f), end = Offset(w * 0.2f, h * 0.5f), strokeWidth = stroke)
        drawLine(color = color, start = Offset(w * 0.8f, h * 0.5f), end = Offset(w, h * 0.5f), strokeWidth = stroke)
    }
}

@Composable
fun RateReviewIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, h * 0.2f),
            size = Size(w * 0.75f, h * 0.65f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = stroke)
        )
        drawLine(color = color, start = Offset(w * 0.15f, h * 0.4f), end = Offset(w * 0.6f, h * 0.4f), strokeWidth = stroke)
        drawLine(color = color, start = Offset(w * 0.15f, h * 0.6f), end = Offset(w * 0.45f, h * 0.6f), strokeWidth = stroke)
        drawLine(
            color = color,
            start = Offset(w * 0.45f, h * 0.45f),
            end = Offset(w * 0.85f, h * 0.05f),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun DistancePinIcon(modifier: Modifier = Modifier, color: Color = LocalContentColor.current) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.28f,
            center = Offset(w * 0.5f, h * 0.35f),
            style = Stroke(width = stroke)
        )
        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.5f, h * 0.85f)
            lineTo(w * 0.75f, h * 0.45f)
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun StarIcon(filled: Boolean, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.tertiary) {
    Canvas(modifier = modifier.size(12.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w * 0.63f, h * 0.38f)
            lineTo(w, h * 0.38f)
            lineTo(w * 0.7f, h * 0.62f)
            lineTo(w * 0.82f, h)
            lineTo(w * 0.5f, h * 0.78f)
            lineTo(w * 0.18f, h)
            lineTo(w * 0.3f, h * 0.62f)
            lineTo(0f, h * 0.38f)
            lineTo(w * 0.37f, h * 0.38f)
            close()
        }
        if (filled) {
            drawPath(path = path, color = color)
        } else {
            drawPath(path = path, color = color, style = Stroke(width = 1.dp.toPx()))
        }
    }
}

@Composable
fun HalfStarIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.tertiary) {
    Canvas(modifier = modifier.size(12.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w * 0.63f, h * 0.38f)
            lineTo(w, h * 0.38f)
            lineTo(w * 0.7f, h * 0.62f)
            lineTo(w * 0.82f, h)
            lineTo(w * 0.5f, h * 0.78f)
            lineTo(w * 0.18f, h)
            lineTo(w * 0.3f, h * 0.62f)
            lineTo(0f, h * 0.38f)
            lineTo(w * 0.37f, h * 0.38f)
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = 1.dp.toPx()))
        clipPath(Path().apply {
            addRect(Rect(0f, 0f, w * 0.5f, h))
        }) {
            drawPath(path = path, color = color)
        }
    }
}
"""

content = content.rstrip() + new_icons + "\n"

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("MapsScreen patch applied successfully!")
