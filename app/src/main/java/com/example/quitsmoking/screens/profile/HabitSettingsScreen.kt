package com.example.quitsmoking.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HabitSettingsScreen(navController: NavController) {

    // ------------------ USER STATE ------------------
    var cigarettesPerDay by remember { mutableStateOf(10) }
    var costPerPack by remember { mutableStateOf(10) }
    var cigarettesPerPack by remember { mutableStateOf(20) }

    // ------------------ SCREEN UI ------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {

        // ---------- TOP BAR ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text("Habit Settings", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Adjust your smoking habits", color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ------------------ CONTENT ------------------
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {

            // ------------------ Cigarettes Per Day ------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE5E5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmokingRooms,
                                contentDescription = null,
                                tint = Color(0xFFCC0000)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text("Cigarettes Per Day", color = Color.Black)
                            Text("Before you quit", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // minus button
                        IconButton(
                            onClick = { cigarettesPerDay = maxOf(1, cigarettesPerDay - 1) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F0F0))
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Gray)
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = cigarettesPerDay.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        // plus button
                        IconButton(
                            onClick = { cigarettesPerDay++ },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F0F0))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ------------------ Cost Per Pack ------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Column(Modifier.padding(20.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5F9F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = "",
                                tint = Color(0xFF009966)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text("Cost Per Pack", color = Color.Black)
                            Text("Average price", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = costPerPack.toString(),
                        onValueChange = {
                            costPerPack = it.toIntOrNull() ?: 0
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ------------------ Cigarettes Per Pack ------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Column(Modifier.padding(20.dp)) {

                    Text("Cigarettes Per Pack", color = Color.Black)
                    Text("Usually 20 or 25", color = Color.Gray, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                        Button(
                            onClick = { cigarettesPerPack = 20 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (cigarettesPerPack == 20) Color(0xFF009966)
                                else Color(0xFFF0F0F0)
                            )
                        ) {
                            Text(
                                "20",
                                color = if (cigarettesPerPack == 20) Color.White else Color.Gray
                            )
                        }

                        Button(
                            onClick = { cigarettesPerPack = 25 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (cigarettesPerPack == 25) Color(0xFF009966)
                                else Color(0xFFF0F0F0)
                            )
                        ) {
                            Text(
                                "25",
                                color = if (cigarettesPerPack == 25) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // ------------------ SAVE BUTTON ------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50.dp),
                onClick = {
                    navController.navigate("profile") {
                        popUpTo("habit_settings") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF009966))
            ) {
                Text("Save Changes", color = Color.White)
            }
        }
    }
}
