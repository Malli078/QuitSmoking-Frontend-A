package com.example.quitsmoking.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    initialPush: Boolean = true,
    initialMilestones: Boolean = true,
    initialDailyReminder: Boolean = true,
    initialEmail: Boolean = true,
    initialSound: Boolean = true,
    initialVibrate: Boolean = true,
    initialDndStart: String = "22:00",
    initialDndEnd: String = "07:00"
) {
    var pushEnabled by remember { mutableStateOf(initialPush) }
    var milestoneEnabled by remember { mutableStateOf(initialMilestones) }
    var dailyReminderEnabled by remember { mutableStateOf(initialDailyReminder) }
    var emailEnabled by remember { mutableStateOf(initialEmail) }
    var soundEnabled by remember { mutableStateOf(initialSound) }
    var vibrateEnabled by remember { mutableStateOf(initialVibrate) }
    var dndStart by remember { mutableStateOf(initialDndStart) }
    var dndEnd by remember { mutableStateOf(initialDndEnd) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Notification Settings",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        },
        bottomBar = {
            Surface(tonalElevation = 6.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    Button(
                        onClick = {
                            // Save changes — placeholder
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Save Changes")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tip: You can also control notifications in the system settings.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Control how and when you receive alerts from the app.",
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingRow("Push Notifications", "Receive push notifications on this device", pushEnabled) { pushEnabled = it }
            Spacer(modifier = Modifier.height(12.dp))
            SettingRow("Milestone Notifications", "Celebrate milestones (1 day, 1 week, etc.)", milestoneEnabled) { milestoneEnabled = it }
            Spacer(modifier = Modifier.height(12.dp))
            SettingRow("Daily Reminder", "A daily check-in / motivation notification", dailyReminderEnabled) { dailyReminderEnabled = it }
            Spacer(modifier = Modifier.height(12.dp))
            SettingRow("Email Notifications", "Receive occasional emails (tips, reports)", emailEnabled) { emailEnabled = it }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Sound & Vibration", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            SettingRow("Sound", "Play a sound when receiving notifications", soundEnabled) { soundEnabled = it }
            Spacer(modifier = Modifier.height(8.dp))
            SettingRow("Vibrate", "Vibrate on notification", vibrateEnabled) { vibrateEnabled = it }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Do Not Disturb", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Notifications will be silenced between the times below.", color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dndStart,
                    onValueChange = { dndStart = it },
                    label = { Text("From") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    supportingText = { Text("HH:mm") }
                )
                OutlinedTextField(
                    value = dndEnd,
                    onValueChange = { dndEnd = it },
                    label = { Text("To") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    supportingText = { Text("HH:mm") }
                )
            }

            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = Color.Gray, fontSize = 13.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
