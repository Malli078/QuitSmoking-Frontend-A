// src/main/java/com/example/quitsmoking/screens/health/EnergyImprovementScreen.kt
package com.example.quitsmoking.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.max
import kotlin.math.min

@Composable
fun EnergyImprovementScreen(navController: NavController, quitDateMillis: Long?) {
    val now = System.currentTimeMillis()
    val quitTs = quitDateMillis ?: now
    val msPerDay = 1000L * 60L * 60L * 24L
    val daysSinceQuit = max(0L, (now - quitTs) / msPerDay).toInt()
    val energyIncrease = min(100, daysSinceQuit * 2)
    var energyLevel by remember { mutableStateOf(7f) } // 1..10 represented as Float

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        // Header gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                    )
                )
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text("Energy Levels", color = Color.White, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text("Feel the energy boost", color = Color.White.copy(alpha = 0.9f))
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Progress Card
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFFFFF7ED), Color(0xFFFFEDD5))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatteryFull,
                                contentDescription = "Battery",
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        // small badge at top-right (zap)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 16.dp, y = (-8).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Zap",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Energy Improvement", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Spacer(Modifier.height(6.dp))
                        Text("+${energyIncrease}%", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF111827))
                    }
                    Spacer(Modifier.height(12.dp))
                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFFEDE9F2))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(energyIncrease / 100f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Info Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Why You Have More Energy", style = MaterialTheme.typography.titleMedium, color = Color(0xFF111827))
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Better oxygen circulation", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Text("• Improved sleep quality", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Text("• Normalized blood sugar levels", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Text("• Reduced carbon monoxide in blood", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Today's Energy Level (slider)
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today's Energy Level", style = MaterialTheme.typography.titleMedium, color = Color(0xFF111827))
                    Spacer(Modifier.height(12.dp))
                    Slider(
                        value = energyLevel,
                        onValueChange = { energyLevel = it.coerceIn(1f, 10f) },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Low", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Text("${energyLevel.toInt()}/10", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF111827))
                        Text("High", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
