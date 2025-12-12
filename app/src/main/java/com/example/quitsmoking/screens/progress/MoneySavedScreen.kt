package com.example.quitsmoking.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt
import kotlin.math.max
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * MoneySavedScreen - Jetpack Compose conversion of the React MoneySavedScreen
 *
 * Parameters:
 *  - navController: NavController (used for a back/home navigation)
 *  - userQuitDateEpochMillis: Long? - epoch millis of user's quit date (optional)
 *  - cigarettesPerDay: Int - user's cigarettes per day (default 10)
 *  - costPerPack: Double - cost per pack (default 10.0)
 *
 * Notes:
 *  - cigarettesPerPack is 20 (matching original)
 *  - This composable is intentionally self-contained and deterministic.
 *  - Replace nav actions with your app navigation as needed.
 */

@Composable
fun MoneySavedScreen(
    navController: NavController,
    userQuitDateEpochMillis: Long? = null,
    cigarettesPerDay: Int = 10,
    costPerPack: Double = 10.0
) {
    // compute days since quit (if null, days = 0)
    val today = LocalDate.now(ZoneId.systemDefault())
    val quitDate = if (userQuitDateEpochMillis != null) {
        Instant.ofEpochMilli(userQuitDateEpochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    } else {
        today
    }
    val daysSinceQuit = max(0, ChronoUnit.DAYS.between(quitDate, today).toInt())

    val cigarettesPerPack = 20.0
    val moneySavedDouble = (cigarettesPerDay / cigarettesPerPack) * costPerPack * daysSinceQuit
    val moneySaved = moneySavedDouble.roundToInt()

    val dailySavingsDouble = (cigarettesPerDay / cigarettesPerPack) * costPerPack
    val dailySavings = (dailySavingsDouble * 100).roundToInt() / 100.0
    val monthlySavings = (dailySavings * 30).roundToInt()
    val yearlySavings = (dailySavings * 365).roundToInt()

    // affordable items (mirrors the React items)
    val affordableItems = listOf(
        AffordableItem("Coffee runs for a month", 150, Icons.Default.LocalCafe, Color(0xFFFEF3C7), Color(0xFF92400E)),
        AffordableItem("Weekend getaway", 500, Icons.Default.Flight, Color(0xFFDBEAFE), Color(0xFF1E3A8A)),
        AffordableItem("New smartphone", 800, Icons.Default.CardGiftcard, Color(0xFFF3E8FF), Color(0xFF5B21B6)),
        AffordableItem("Dream vacation", 2000, Icons.Default.Flight, Color(0xFFD1FAE5), Color(0xFF065F46)),
        AffordableItem("Down payment savings", 5000, Icons.Default.Home, Color(0xFFD1FAE5), Color(0xFF065F46))
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // Header: gradient + back button text (you can replace with IconButton)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF10B981), Color(0xFF059669))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // simple "Back" text - replace with IconButton if desired
                        Button(
                            onClick = { navController.navigate("home") },
                            modifier = Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                        ) {
                            Text("Back")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Money Saved",
                        color = Color.White,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Watch your savings grow every day!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            // content container with negative offset feel (like -mt-6)
            Column(modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-24).dp)) {
                // Total Saved Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Saved", color = Color(0xFF6B7280), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$${moneySaved}", color = Color(0xFF111827), fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("In $daysSinceQuit days", color = Color(0xFF6B7280), fontSize = 12.sp)
                        }

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    brush = Brush.verticalGradient(listOf(Color(0xFF34D399), Color(0xFF059669))),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AttachMoney, contentDescription = "Money", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }

                    // daily/monthly/yearly row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Daily", color = Color(0xFF6B7280), fontSize = 12.sp)
                            Text("$${dailySavings}", color = Color(0xFF111827))
                        }
                        Column {
                            Text("Monthly", color = Color(0xFF6B7280), fontSize = 12.sp)
                            Text("$${monthlySavings}", color = Color(0xFF111827))
                        }
                        Column {
                            Text("Yearly", color = Color(0xFF6B7280), fontSize = 12.sp)
                            Text("$${yearlySavings}", color = Color(0xFF111827))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Savings Trend (simple seven bars)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF059669))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Savings Trend", color = Color(0xFF111827))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            // create 7 bars; heights are illustrative (increase)
                            for (i in 0 until 7) {
                                val heightPercent = ((i + 1) / 7.0).coerceAtMost(1.0)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight(fraction = heightPercent.toFloat())
                                            .fillMaxWidth()
                                            .background(
                                                brush = Brush.verticalGradient(listOf(Color(0xFF34D399), Color(0xFF10B981))),
                                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                            )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            for (i in 0 until 7) {
                                Text("Day ${i + 1}", color = Color(0xFF6B7280), fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // What You Could Afford
                Text(text = "What You Could Afford", color = Color(0xFF111827), fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    affordableItems.forEach { item ->
                        val canAfford = moneySaved >= item.cost
                        val progress = (moneySaved.toDouble() / item.cost * 100).coerceAtMost(100.0).roundToInt()

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (canAfford) 6.dp else 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(color = item.bgColor, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(item.icon, contentDescription = item.name, tint = item.fgColor)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, color = Color(0xFF111827))
                                        Text("$${item.cost}", color = Color(0xFF6B7280), fontSize = 12.sp)
                                    }

                                    if (canAfford) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(color = Color(0xFF10B981), shape = CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Can afford",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(6.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = (progress / 100f))
                                            .height(8.dp)
                                            .background(Color(0xFF10B981), shape = RoundedCornerShape(6.dp))
                                    )
                                }

                                if (!canAfford) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "$${max(0, item.cost - moneySaved)} more to go",
                                        color = Color(0xFF6B7280),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Motivation / 10-year projection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899)))
                            )
                            .padding(16.dp)
                    ) {
                        Text("10-Year Projection", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "$${yearlySavings * 10}", color = Color.White, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Imagine what you could do with all that money! 🎯",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            } // end content column
        } // end root column
    } // end Surface
}

// small helper data class for items
private data class AffordableItem(
    val name: String,
    val cost: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val bgColor: Color,
    val fgColor: Color
)
