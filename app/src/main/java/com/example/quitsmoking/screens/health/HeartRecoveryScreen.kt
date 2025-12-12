// src/main/java/com/example/quitsmoking/screens/health/HeartRecoveryScreen.kt
package com.example.quitsmoking.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.max
import kotlin.math.min

private data class HeartMilestone(val days: Int, val time: String, val desc: String)

@Composable
fun HeartRecoveryScreen(navController: NavController, quitDateMillis: Long?) {

    val now = System.currentTimeMillis()
    val quitTs = quitDateMillis ?: now
    val msPerDay = 1000L * 60L * 60L * 24L
    val daysSinceQuit = max(0L, (now - quitTs) / msPerDay).toInt()

    val currentProgressDouble = min(100.0, (daysSinceQuit.toDouble() / 365.0) * 100.0)
    val progressFraction = (currentProgressDouble / 100.0).toFloat()

    val milestones = listOf(
        HeartMilestone(0, "20 Minutes", "Heart rate returns to normal"),
        HeartMilestone(1, "12 Hours", "Blood pressure normalizes"),
        HeartMilestone(14, "2 Weeks", "Circulation improves"),
        HeartMilestone(90, "3 Months", "Heart attack risk starts to drop"),
        HeartMilestone(365, "1 Year", "Heart disease risk cut in half"),
        HeartMilestone(1825, "5 Years", "Stroke risk reduced to non-smoker level")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFB7185), Color(0xFFF472B6))
                    )
                )
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Column(modifier = Modifier.align(Alignment.CenterStart).padding(16.dp)) {
                Text("Heart Recovery", color = Color.White, style = MaterialTheme.typography.titleLarge)
                Text("Your heart is healing", color = Color.White.copy(alpha = 0.9f))
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // PROGRESS CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE4E6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFBE123C), modifier = Modifier.size(28.dp))
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text("Heart Recovery Progress", color = Color.Gray)
                            Text("${currentProgressDouble.toInt()}%", style = MaterialTheme.typography.headlineMedium)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFE5E7EB))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .height(10.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFB7185), Color(0xFFF43F5E))
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // INFO CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFBE123C))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Amazing Progress!", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Your cardiovascular system is rapidly improving. Keep going!",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // MILESTONES
            Text("Recovery Milestones", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                milestones.forEach { ms ->
                    val completed = daysSinceQuit >= ms.days

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (completed) Color(0xFFE6FFFA) else Color.White
                        )
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {

                            // ICONS
                            if (completed) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color(0xFFD1D5DB), CircleShape)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    ms.time,
                                    color = if (completed) Color(0xFF065F46) else Color.Black
                                )
                                Text(
                                    ms.desc,
                                    color = if (completed) Color(0xFF047857) else Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
