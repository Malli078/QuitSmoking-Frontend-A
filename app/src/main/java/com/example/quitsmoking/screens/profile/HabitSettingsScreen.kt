package com.example.quitsmoking.screens.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quitsmoking.viewmodel.HabitViewModel

@Composable
fun HabitSettingsScreen(
    navController: NavController,
    viewModel: HabitViewModel = viewModel()
) {
    val context = LocalContext.current

    // 🔐 USER ID
    val userPrefs =
        context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    val userId = userPrefs.getInt("user_id", 0)

    // 💾 LOCAL PREFS (USED BY PROFILE SCREEN)
    val prefs =
        context.getSharedPreferences("user_habits", Context.MODE_PRIVATE)

    // UI STATE
    var cigarettesPerDay by remember { mutableStateOf(10) }
    var costPerPack by remember { mutableStateOf(10) }

    // 🚨 NOT BACKEND — LOCAL ONLY
    var cigarettesPerPack by remember {
        mutableStateOf(prefs.getInt("cigarettes_per_pack", 20))
    }

    // BACKEND STATE
    val habits by viewModel.habits.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // LOAD FROM BACKEND
    LaunchedEffect(Unit) {
        if (userId > 0) {
            viewModel.loadHabits(userId)
        }
    }

    // BACKEND → UI
    LaunchedEffect(habits) {
        habits?.let {
            cigarettesPerDay = it.cigarettes_per_day
            costPerPack = it.cost_per_pack.toInt()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {

        // 🔙 TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Habit Settings", style = MaterialTheme.typography.headlineMedium)
            Text("Adjust your smoking habits", color = Color.Gray)
            Spacer(Modifier.height(20.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {

            // 🚬 CIGARETTES PER DAY
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Cigarettes Per Day")
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        IconButton(
                            onClick = {
                                cigarettesPerDay = maxOf(1, cigarettesPerDay - 1)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        ) {
                            Icon(Icons.Default.Remove, null)
                        }

                        Spacer(Modifier.width(20.dp))

                        Text(
                            cigarettesPerDay.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(Modifier.width(20.dp))

                        IconButton(
                            onClick = { cigarettesPerDay++ },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        ) {
                            Icon(Icons.Default.Add, null)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 💰 COST PER PACK
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Cost Per Pack")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = costPerPack.toString(),
                        onValueChange = {
                            costPerPack = it.toIntOrNull() ?: costPerPack
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 📦 CIGARETTES PER PACK (LOCAL ONLY)
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Cigarettes Per Pack")
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(20, 25).forEach { value ->
                            Button(
                                onClick = { cigarettesPerPack = value },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        if (cigarettesPerPack == value)
                                            Color(0xFF009966)
                                        else Color.LightGray
                                )
                            ) {
                                Text(
                                    value.toString(),
                                    color =
                                        if (cigarettesPerPack == value)
                                            Color.White
                                        else Color.Black
                                )
                            }
                        }
                    }
                }
            }

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Color.Red)
            }
        }

        // 💾 SAVE BUTTON
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    // BACKEND SAVE (ONLY WHAT BACKEND SUPPORTS)
                    viewModel.saveHabits(
                        userId = userId,
                        cigarettesPerDay = cigarettesPerDay,
                        costPerPack = costPerPack.toDouble(),
                        quitDate = null
                    ) {
                        // LOCAL SAVE (PROFILE SCREEN USES THIS)
                        prefs.edit()
                            .putInt("cigarettes_per_day", cigarettesPerDay)
                            .putInt("cost_per_pack", costPerPack)
                            .putInt("cigarettes_per_pack", cigarettesPerPack)
                            .apply()

                        navController.navigate("profile") {
                            popUpTo("habit_settings") { inclusive = true }
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF009966))
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Save Changes", color = Color.White)
                }
            }
        }
    }
}
