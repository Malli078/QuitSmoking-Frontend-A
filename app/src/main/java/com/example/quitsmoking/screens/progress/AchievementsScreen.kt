package com.example.quitsmoking.screens.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.max
import kotlin.math.roundToInt

private data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val unlocked: Boolean,
    val dateLabel: String
)

@Composable
fun AchievementsScreen(
    navController: NavController,
    userQuitDateEpochMillis: Long? = null
) {
    // compute today / quit date
    val today = LocalDate.now(ZoneId.systemDefault())
    val quitDate: LocalDate = remember(userQuitDateEpochMillis) {
        if (userQuitDateEpochMillis != null) {
            Instant.ofEpochMilli(userQuitDateEpochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        } else {
            LocalDate.now(ZoneId.systemDefault())
        }
    }

    val daysSinceQuit by remember(quitDate) {
        derivedStateOf {
            max(
                0,
                java.time.temporal.ChronoUnit.DAYS.between(quitDate, today).toInt()
            )
        }
    }

    // prepare achievements
    val achievements = remember(daysSinceQuit) {
        listOf(
            AchievementItem(
                id = "first-day",
                title = "First Day",
                description = "Completed your first smoke-free day",
                icon = Icons.Default.Star,
                color = Color(0xFFFFD54F),
                unlocked = daysSinceQuit >= 1,
                dateLabel = if (daysSinceQuit >= 1) "Today" else "Locked"
            ),
            AchievementItem(
                id = "three-days",
                title = "Three Day Warrior",
                description = "Nicotine is leaving your body",
                icon = Icons.Default.FlashOn,
                color = Color(0xFF3B82F6),
                unlocked = daysSinceQuit >= 3,
                dateLabel = if (daysSinceQuit >= 3) "${(daysSinceQuit - 3)} days ago" else "Locked"
            ),
            AchievementItem(
                id = "one-week",
                title = "Week Champion",
                description = "One full week smoke-free!",
                icon = Icons.Default.MilitaryTech,
                color = Color(0xFF8B5CF6),
                unlocked = daysSinceQuit >= 7,
                dateLabel = if (daysSinceQuit >= 7) "Unlocked" else "${7 - daysSinceQuit} days to go"
            ),
            AchievementItem(
                id = "two-weeks",
                title = "Fortnight Master",
                description = "Your circulation is improving",
                icon = Icons.Default.EmojiEvents,
                color = Color(0xFF14B8A6),
                unlocked = daysSinceQuit >= 14,
                dateLabel = if (daysSinceQuit >= 14) "Unlocked" else "${14 - daysSinceQuit} days to go"
            ),
            AchievementItem(
                id = "one-month",
                title = "Monthly Milestone",
                description = "One month of freedom!",
                icon = Icons.Default.LocalOffer,
                color = Color(0xFF10B981),
                unlocked = daysSinceQuit >= 30,
                dateLabel = if (daysSinceQuit >= 30) "Unlocked" else "${30 - daysSinceQuit} days to go"
            ),
            AchievementItem(
                id = "three-months",
                title = "Quarter Year Legend",
                description = "Lung function increased by 30%",
                icon = Icons.Default.Star,
                color = Color(0xFF6366F1),
                unlocked = daysSinceQuit >= 90,
                dateLabel = if (daysSinceQuit >= 90) "Unlocked" else "${90 - daysSinceQuit} days to go"
            ),
            AchievementItem(
                id = "six-months",
                title = "Half Year Hero",
                description = "Your risk dropped significantly",
                icon = Icons.Default.EmojiEvents,
                color = Color(0xFFEC4899),
                unlocked = daysSinceQuit >= 180,
                dateLabel = if (daysSinceQuit >= 180) "Unlocked" else "${180 - daysSinceQuit} days to go"
            ),
            AchievementItem(
                id = "one-year",
                title = "Annual Champion",
                description = "Heart disease risk cut in half!",
                icon = Icons.Default.Verified,
                color = Color(0xFFFFB020),
                unlocked = daysSinceQuit >= 365,
                dateLabel = if (daysSinceQuit >= 365) "Unlocked" else "${365 - daysSinceQuit} days to go"
            )
        )
    }

    val unlockedCount = achievements.count { it.unlocked }
    val progressFraction by animateFloatAsState(targetValue = if (achievements.isEmpty()) 0f else unlockedCount.toFloat() / achievements.size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFF97316))))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "←",
                    color = Color.White,
                    modifier = Modifier
                        .clickable { navController.navigate("home") }
                        .padding(8.dp),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Achievement Badges", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "$unlockedCount of ${achievements.size} unlocked", color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        // Add a bit of space so the progress card isn't glued to the header
        Spacer(modifier = Modifier.height(8.dp))

        // Content area (small overlap but with breathing room)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-12).dp) // reduced overlap
        ) {
            // Progress Card (moved slightly down by the Spacer above)
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Overall Progress", color = Color.Gray)
                        Text("${(progressFraction * 100).roundToInt()}%", color = Color(0xFF111827), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F4F6))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressFraction)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFF97316))))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Achievements Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 16.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements) { ach ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (ach.unlocked) Color.White else Color(0xFFF3F4F6)),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (ach.unlocked) 6.dp else 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(if (ach.unlocked) ach.color else Color(0xFF9CA3AF)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (ach.unlocked) {
                                    Icon(ach.icon, contentDescription = ach.title, tint = Color.White, modifier = Modifier.size(28.dp))
                                } else {
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = ach.title, color = Color(0xFF111827), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = ach.description, color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = ach.dateLabel, color = if (ach.unlocked) Color(0xFF10B981) else Color(0xFF6B7280), fontSize = 12.sp)
                        }
                    }
                }

                // Next achievement full-width card (spans 2 columns)
                if (unlockedCount < achievements.size) {
                    item(span = { GridItemSpan(2) }) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val next = achievements.firstOrNull { !it.unlocked }
                        next?.let { nxt ->
                            // Put the gradient on the card itself so the rounded corners are fully colored
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899))))
                                    .clickable { navController.navigate("achievement_detail/${nxt.id}") },
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Next Achievement", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(nxt.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(nxt.dateLabel, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } // end LazyVerticalGrid
        } // end content column
    } // end root column
}
