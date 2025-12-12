package com.example.quitsmoking.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.max
import kotlin.math.roundToInt

/* ---------------------- Models ---------------------- */
data class User(
    val id: String,
    val name: String,
    val quitDateEpochMillis: Long? = null,
    val cigarettesPerDay: Int? = 10,
    val costPerPack: Double? = 10.0
)

data class Craving(
    val id: String,
    val timestampEpochMillis: Long,
    val overcome: Boolean = false
)

/* ---------------------- ViewModel ---------------------- */
class HomeViewModel : ViewModel() {
    private val _user = MutableStateFlow(
        User(
            id = "u1",
            name = "Friend",
            quitDateEpochMillis = Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli(),
            cigarettesPerDay = 10,
            costPerPack = 12.0
        )
    )
    val user: StateFlow<User?> = _user

    private val _streak = MutableStateFlow(10)
    val streak: StateFlow<Int> = _streak

    private val _cravings = MutableStateFlow(
        listOf(
            Craving("c1", Instant.now().minus(9, ChronoUnit.DAYS).toEpochMilli(), overcome = true),
            Craving("c2", Instant.now().minus(6, ChronoUnit.DAYS).toEpochMilli(), overcome = false),
            Craving("c3", Instant.now().minus(2, ChronoUnit.DAYS).toEpochMilli(), overcome = true)
        )
    )
    val cravings: StateFlow<List<Craving>> = _cravings
}

/* ---------------------- HomeScreen ---------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val cravings by viewModel.cravings.collectAsState()

    val daysSinceQuit = remember(user) {
        val quitMillis = user?.quitDateEpochMillis
        val today = LocalDate.now(ZoneId.systemDefault())
        if (quitMillis != null) {
            val quitDate = Instant.ofEpochMilli(quitMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            max(0L, ChronoUnit.DAYS.between(quitDate, today))
        } else 0L
    }

    val cigarettesAvoided = remember(daysSinceQuit, user) {
        daysSinceQuit * (user?.cigarettesPerDay?.toLong() ?: 10L)
    }

    val moneySaved = remember(cigarettesAvoided, user) {
        val costPerPack = user?.costPerPack ?: 10.0
        val saved = (cigarettesAvoided.toDouble() / 20.0) * costPerPack
        max(0, saved.roundToInt())
    }

    val cravingsResisted = remember(cravings) {
        cravings.count { it.overcome }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, ${user?.name ?: "Friend"}! 👋",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "You're doing amazing!",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        BadgedBox(badge = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                            )
                        }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            HomeBottomNavigation(navController = navController)
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            /* ------------------- Main Stats Card ------------------- */
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Smoke-free for", style = MaterialTheme.typography.bodySmall)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "$daysSinceQuit",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("days", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "Flame",
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Cigarettes avoided", style = MaterialTheme.typography.labelSmall)
                            Text("$cigarettesAvoided", style = MaterialTheme.typography.titleMedium)
                        }
                        Column {
                            Text("Money saved", style = MaterialTheme.typography.labelSmall)
                            Text("\$${moneySaved}", style = MaterialTheme.typography.titleMedium)
                        }
                        Column {
                            Text("Streak", style = MaterialTheme.typography.labelSmall)
                            Text("${streak} days", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------------- Quick Actions ---------------- */
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        label = "Log Craving",
                        icon = Icons.Default.Place,
                        onClick = { navController.navigate("craving-alert") }
                    )
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        label = "AI Support",
                        icon = Icons.AutoMirrored.Filled.Message,
                        onClick = { navController.navigate("ai_chat") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        label = "Craving Predictions",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        onClick = { navController.navigate("craving_prediction") }
                    )
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        label = "Daily Progress", // NEW quick action
                        icon = Icons.Default.Assessment, // <-- replaced Target with Assessment
                        onClick = { navController.navigate("daily_progress") } // <-- new route
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------------- Daily Motivation (fixed route) ---------------- */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("daily_motivation") },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today's Motivation", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "\"Every moment you resist is a victory. Your body is healing right now.\"",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Tap for more inspiration →", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------------- Today's Goals ---------------- */
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Today's Goals", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { navController.navigate("todays_goals") }) {
                            Text("View All", color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        GoalRow(text = "Morning breathing exercise", completed = true)
                        GoalRow(text = "Log afternoon check-in", completed = false)
                        GoalRow(text = "Evening gratitude journal", completed = false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/* ---------------------- Buttons / Rows ---------------------- */
@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun GoalRow(text: String, completed: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (completed) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = "done", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimary)
            }
            Text(text, style = MaterialTheme.typography.bodyMedium)
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.outline)
            )
            Text(text, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}
