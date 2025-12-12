// src/main/java/com/example/quitsmoking/screens/health/AIHealthPredictionScreen.kt
package com.example.quitsmoking.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException
import kotlin.math.min
import kotlin.math.roundToInt

// NOTE: DO NOT declare `data class User` here — reuse the User declared in HealthDashboardScreen.kt

private data class Prediction(
    val metric: String,
    val current: Float,
    val predicted: Float,
    val timeframe: String,
    val colorKey: String,
    val iconType: IconType
)

private enum class IconType { HEART, LUNG, BATTERY, TREND }

@Composable
fun AIHealthPredictionScreen(navController: NavController, user: User?) {
    val quitInstant = remember(user) {
        parseQuitInstantOrNow(user?.quitDateIso)
    }

    val daysSinceQuit = remember(quitInstant) {
        Duration.between(quitInstant, Instant.now()).toDays().coerceAtLeast(0L).toInt()
    }

    // Build predictions (mirrors your JS math)
    val predictions = remember(daysSinceQuit) {
        listOf(
            Prediction(
                metric = "Heart Health",
                current = min(70f + daysSinceQuit * 0.5f, 95f),
                predicted = min(80f + daysSinceQuit * 0.5f, 100f),
                timeframe = "30 days",
                colorKey = "rose",
                iconType = IconType.HEART
            ),
            Prediction(
                metric = "Lung Function",
                current = min(60f + daysSinceQuit * 0.7f, 90f),
                predicted = min(75f + daysSinceQuit * 0.7f, 100f),
                timeframe = "60 days",
                colorKey = "cyan",
                iconType = IconType.LUNG
            ),
            Prediction(
                metric = "Energy Levels",
                current = min(65f + daysSinceQuit * 0.6f, 92f),
                predicted = min(85f + daysSinceQuit * 0.6f, 100f),
                timeframe = "45 days",
                colorKey = "amber",
                iconType = IconType.BATTERY
            )
        )
    }

    // map colorKey -> colors (background, icon tint)
    val colorMap = mapOf(
        "rose" to Pair(Color(0xFFFFE4E6), Color(0xFFBE123C)),
        "cyan" to Pair(Color(0xFFE6FFFA), Color(0xFF0891B2)),
        "amber" to Pair(Color(0xFFFFFBEB), Color(0xFFB45309))
    )

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        // Header gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF6366F1), Color(0xFF7C3AED))))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text("AI Health Prediction", color = Color.White, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Your personalized forecast", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Body
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            // Intro card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF))))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "AI",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("AI Analysis", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Based on your progress and data from thousands of successful quitters, here's what to expect.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Predictions list
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                predictions.forEach { p ->
                    val colors = colorMap[p.colorKey] ?: Pair(Color.LightGray, Color.DarkGray)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.first),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (p.iconType) {
                                        IconType.HEART -> Icon(Icons.Default.Favorite, contentDescription = null, tint = colors.second)
                                        IconType.LUNG -> Icon(Icons.Default.TrendingUp, contentDescription = null, tint = colors.second)
                                        IconType.BATTERY -> Icon(Icons.Default.BatteryFull, contentDescription = null, tint = colors.second)
                                        IconType.TREND -> Icon(Icons.Default.TrendingUp, contentDescription = null, tint = colors.second)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(p.metric, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Predicted in ${p.timeframe}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                                }

                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF10B981))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Current
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Current", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                                Text("${p.current.roundToInt()}%", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = (p.current.coerceIn(0f, 100f) / 100f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                trackColor = Color(0xFFF1F5F9)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Predicted
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Predicted", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                                Text("${p.predicted.roundToInt()}%", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF10B981))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = (p.predicted.coerceIn(0f, 100f) / 100f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = Color(0xFF10B981),
                                trackColor = Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tips card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("To Maximize Recovery", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Stay consistent with your quit journey", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Text("• Exercise regularly to boost lung health", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Text("• Stay hydrated (8+ glasses daily)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Text("• Get 7-9 hours of quality sleep", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        Text("• Practice breathing exercises daily", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    }
                }
            }
        }
    }
}

// Helper: parse ISO or epoch millis, fallback to now
private fun parseQuitInstantOrNow(iso: String?): Instant {
    if (iso == null) return Instant.now()
    return try {
        Instant.parse(iso)
    } catch (e: DateTimeParseException) {
        try {
            Instant.ofEpochMilli(iso.toLong())
        } catch (_: Exception) {
            Instant.now()
        }
    }
}
