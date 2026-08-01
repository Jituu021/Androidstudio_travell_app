package com.example.travel.gis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travel.gis.domain.model.OfflineRegion

@Composable
fun OfflineDownloadModal(
    regions: List<OfflineRegion>,
    downloadProgress: Pair<String, Int>,
    onDownloadRegion: (OfflineRegion) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📥", fontSize = 20.sp)
                Text("OFFLINE MAP PACKS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Download offline map regions to navigate seamlessly without mobile data.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(regions) { region ->
                        val (activeId, percent) = downloadProgress
                        val isDownloading = activeId == region.id && percent < 100

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (region.isDownloaded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(region.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${region.sizeMb} MB • Zoom ${region.minZoom}-${region.maxZoom}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    if (region.isDownloaded) {
                                        Text("✓ READY", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Button(
                                            onClick = { onDownloadRegion(region) },
                                            enabled = !isDownloading,
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(if (isDownloading) "$percent%" else "DOWNLOAD", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (isDownloading) {
                                    LinearProgressIndicator(
                                        progress = { percent / 100f },
                                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", fontWeight = FontWeight.Bold)
            }
        }
    )
}
