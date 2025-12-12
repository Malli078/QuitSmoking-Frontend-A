package com.example.quitsmoking.screens.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quitsmoking.screens.profile.UserProfile
import java.text.SimpleDateFormat
import java.util.*

data class Milestone(val days: Int, val label: String, val enabled: Boolean)

@Composable
fun QuitPlanSettingsScreen(
    navController: NavController,
    user: UserProfile? = null
) {
    val initial = listOf(
        Milestone(1, "24 Hours", true),
        Milestone(3, "3 Days", true),
        Milestone(7, "1 Week", true),
        Milestone(14, "2 Weeks", true),
        Milestone(30, "1 Month", true),
        Milestone(90, "3 Months", true),
        Milestone(365, "1 Year", true),
    )

    val milestones = remember { initial.toMutableStateList() }

    fun toggleMilestone(index: Int) {
        val m = milestones[index]
        milestones[index] = m.copy(enabled = !m.enabled)
    }

    // Quit date formatting
    val quitDateDisplay = remember(user?.quitDate) {
        user?.quitDate?.let { raw ->
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd",
                "yyyy-MM-dd HH:mm:ss"
            )
            var parsed: Date? = null
            for (fmt in formats) {
                try {
                    val sdf = SimpleDateFormat(fmt, Locale.getDefault())
                    parsed = sdf.parse(raw)
                    if (parsed != null) break
                } catch (_: Exception) { }
            }
            parsed?.let {
                val outFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                outFormat.format(it)
            } ?: "Not set"
        } ?: "Not set"
    }

    // SCROLL ENABLED HERE 👇
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB))
            .verticalScroll(rememberScrollState())
    ) {

        // Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 48.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.DarkGray
                )
            }
        }

        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Quit Plan", style = MaterialTheme.typography.headlineMedium)
            Text("Manage your milestones", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
        }

        // Quit Date Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF059669))
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CalendarToday,
                        contentDescription = "Quit Date",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Quit Date", color = Color.White)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    quitDateDisplay,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Milestones Title
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text("Milestone Notifications", style = MaterialTheme.typography.titleMedium)
            Text("Choose which milestones to celebrate", color = Color.Gray, fontSize = 13.sp)
        }

        Spacer(Modifier.height(12.dp))

        // Milestone List
        Column(Modifier.padding(horizontal = 16.dp)) {
            milestones.forEachIndexed { index, milestone ->
                MilestoneRow(
                    milestone = milestone,
                    onToggle = { toggleMilestone(index) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun MilestoneRow(
    milestone: Milestone,
    onToggle: () -> Unit
) {
    val circleOffset by animateDpAsState(
        targetValue = if (milestone.enabled) 24.dp else 2.dp
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Left Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (milestone.enabled) Color(0xFFD1FAE5)
                        else Color(0xFFF3F4F6)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (milestone.enabled) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF059669)
                    )
                } else {
                    Icon(
                        Icons.Filled.Flag,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Center Texts
            Column(Modifier.weight(1f)) {
                Text(milestone.label, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${milestone.days} days smoke-free",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Toggle Pill
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(26.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (milestone.enabled) Color(0xFF059669)
                        else Color(0xFFD1D5DB)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .offset(x = circleOffset, y = 2.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}
