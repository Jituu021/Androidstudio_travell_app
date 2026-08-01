package com.example.travel.gis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travel.TravelExpense

@Composable
fun GisExpenseTrackerModal(
    userLocationName: String,
    expensesList: List<TravelExpense>,
    onAddExpense: (Double, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Fuel") }
    var descriptionInput by remember { mutableStateOf("Expense at $userLocationName") }

    val categories = listOf("Fuel", "Food", "Hotel", "Shopping", "Toll")
    val totalExpenseSum = expensesList.sumOf { it.amount }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("💰", fontSize = 20.sp)
                Text("GPS TRIP EXPENSE SPLITTER", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Expense Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL TRIP SPENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("₹${String.format("%.2f", totalExpenseSum)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("📍 Tagged: $userLocationName", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1)
                    }
                }

                // Amount & Category Input
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (₹)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Filter Chips
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                Button(
                    onClick = {
                        val amount = amountInput.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onAddExpense(amount, selectedCategory, descriptionInput)
                            amountInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("ADD GPS EXPENSE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Expense History List
                Text("RECENT LOGGED EXPENSES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(expensesList) { expense ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(expense.description, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("${expense.category} • ${expense.timestamp}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("₹${expense.amount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("DONE", fontWeight = FontWeight.Bold)
            }
        }
    )
}
