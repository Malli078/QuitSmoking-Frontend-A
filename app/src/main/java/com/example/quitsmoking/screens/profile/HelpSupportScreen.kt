@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.quitsmoking.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private const val DBG = "HELP_DBG"

data class HelpTopic(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val desc: String
)

@Composable
fun HelpSupportScreen(navController: NavController) {
    Log.d(DBG, "Composing HelpSupportScreen")

    // track composition lifecycle
    DisposableEffect(Unit) {
        Log.d(DBG, "HelpSupportScreen entered composition")
        onDispose { Log.d(DBG, "HelpSupportScreen disposed") }
    }

    // Topics - safe, no navigation side effects here
    val helpTopics = remember {
        listOf(
            HelpTopic(Icons.Filled.Book, "Getting Started Guide", "Learn the basics"),
            HelpTopic(Icons.AutoMirrored.Filled.Help, "FAQs", "Common questions answered"),
            HelpTopic(Icons.Filled.Language, "Community Forum", "Connect with others"),
            HelpTopic(Icons.AutoMirrored.Filled.Message, "Live Chat Support", "24/7 assistance")
        )
    }

    // IMPORTANT: no automatic nav calls here. Only user clicks cause navigation.
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = {
                    // use safePop so we log and guard errors
                    IconButton(onClick = {
                        Log.d(DBG, "Back icon clicked -> safePop()")
                        safePop(navController)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF9FAFB))
                .padding(16.dp)
        ) {
            Text("We're here to assist you", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))

            // Hero card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Message,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Need Help?", fontSize = 18.sp, color = Color.Black)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Our support team is available 24/7 to help you succeed on your quit journey.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        Log.d(DBG, "Start Chat clicked -> will navigate to ai_chat")
                        safeNavigate(navController, "ai_chat")
                    }) {
                        Text("Start Chat")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Help Topics", fontSize = 16.sp, color = Color.Black)
            Spacer(Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                helpTopics.forEachIndexed { index, topic ->
                    TopicButton(
                        icon = topic.icon,
                        title = topic.title,
                        desc = topic.desc,
                        onClick = {
                            Log.d(DBG, "Topic clicked index=$index title=${topic.title}")
                            safeNavigate(navController, "help_topic_$index")
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Contact Us", fontSize = 16.sp, color = Color.Black)
            Spacer(Modifier.height(12.dp))

            ContactRow(Icons.Filled.Email, "Email", "support@quitapp.com")
            Spacer(Modifier.height(8.dp))
            ContactRow(Icons.Filled.Phone, "Hotline", "1-800-QUIT-NOW")

            Spacer(Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("App Version 1.0.0", color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Text("© 2025 Quit Smoking App", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

/** simple safe wrappers that log failures */
private fun safeNavigate(navController: NavController, route: String) {
    try {
        Log.d(DBG, "Attempt navigate -> $route")
        navController.navigate(route)
    } catch (e: Exception) {
        Log.e(DBG, "navigate to $route failed", e)
    }
}
private fun safePop(navController: NavController) {
    try {
        Log.d(DBG, "Attempt popBackStack()")
        navController.popBackStack()
    } catch (e: Exception) {
        Log.e(DBG, "popBackStack failed", e)
    }
}

@Composable
private fun TopicButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFF3F4F6)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF374151))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, color = Color.Black)
                Text(desc, fontSize = 13.sp, color = Color.Gray)
            }
            Text("→", fontSize = 20.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFD1FAE5)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF059669))
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(title, fontSize = 14.sp, color = Color.Black)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}
