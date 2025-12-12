// src/main/java/com/example/quitsmoking/screens/health/TasteSmellRecoveryScreen.kt
package com.example.quitsmoking.screens.health

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.min
import kotlin.math.floor

/**
 * Kotlin/Jetpack Compose conversion of the React TasteSmellRecoveryScreen.
 *
 * Usage:
 *   composable("taste-smell-recovery") {
 *     TasteSmellRecoveryScreen(navController = navController, quitDateMillis = /* nullable Long */)
 *   }
 */
@Composable
fun TasteSmellRecoveryScreen(
    navController: NavController,
    quitDateMillis: Long?
) {
    val now = System.currentTimeMillis()
    val quit = quitDateMillis ?: now
    val daysSinceQuit = floor((now - quit) / (1000 * 60 * 60 * 24f)).toInt().coerceAtLeast(0)
    val recoveryPercent = min((daysSinceQuit / 30.0 * 100.0).toInt(), 100)

    // experiences similar to the JS version — use built-in material icons
    data class Experience(val icon: ImageVector, val label: String, val unlocked: Boolean)

    val experiences = listOf(
        Experience(Icons.Default.LocalCafe, "Coffee aroma", daysSinceQuit >= 2),
        Experience(Icons.Default.Fastfood, "Food flavors", daysSinceQuit >= 3),
        Experience(Icons.Default.LocalFlorist, "Fresh flowers", daysSinceQuit >= 5),
        Experience(Icons.Default.TagFaces, "Full taste & smell", daysSinceQuit >= 30)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
                    )
                )
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 0.dp)
            ) {
                Text(
                    text = "Taste & Smell",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your senses are returning",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Progress card
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFF3E8FF), Color(0xFFFDE8F0))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TagFaces,
                            contentDescription = "Smile",
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Senses Recovery", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "${recoveryPercent}%", style = MaterialTheme.typography.headlineMedium)

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = recoveryPercent / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info card
            Card(
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F0FF))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Good News!", style = MaterialTheme.typography.titleMedium, color = Color(0xFF111827))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your taste buds and smell receptors regenerate within 48 hours. Full recovery takes about 1 month.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Experiences Unlocked", style = MaterialTheme.typography.titleMedium, color = Color(0xFF111827))
            Spacer(modifier = Modifier.height(12.dp))

            // 2-column grid replacement using rows (keeps code simple)
            val chunked = experiences.chunked(2)
            chunked.forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { exp ->
                        val containerColor = if (exp.unlocked) Color(0xFFECFDF5) else Color.White
                        val borderStroke = if (exp.unlocked) BorderStroke(1.dp, Color(0xFFBBF7D0)) else BorderStroke(1.dp, Color(0xFFE5E7EB))

                        Card(
                            modifier = Modifier
                                .weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(containerColor = containerColor)
                        ) {
                            Column(modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                                .border(borderStroke, shape = RoundedCornerShape(12.dp))
                            ) {
                                Box(modifier = Modifier.height(36.dp)) {
                                    Icon(
                                        imageVector = exp.icon,
                                        contentDescription = exp.label,
                                        tint = if (exp.unlocked) Color(0xFF059669) else Color(0xFF9CA3AF),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    if (exp.unlocked) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Unlocked",
                                            tint = Color(0xFF059669),
                                            modifier = Modifier
                                                .size(18.dp)
                                                .align(Alignment.TopEnd)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = exp.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (exp.unlocked) Color(0xFF065F46) else Color(0xFF6B7280)
                                )
                            }
                        }
                    }

                    // if rowItems size < 2, add a spacer to keep layout consistent
                    if (rowItems.size < 2) Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
