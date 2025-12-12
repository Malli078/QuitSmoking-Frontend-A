@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.quitsmoking.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PrivacySecurityScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Privacy & Security") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                .padding(padding) // scaffold insets first
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // <--- make it scrollable
                .background(Color(0xFFF9FAFB))
                .padding(16.dp)
        ) {
            Text("Your data is protected", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))

            // GREEN INFO CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F8EE)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34D399).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF059669)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Your Privacy Matters", fontSize = 16.sp, color = Color.Black)
                        Text(
                            "All your data is encrypted and stored securely. We never share your personal information.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // MENU BUTTONS
            MenuButton(
                title = "Privacy Policy",
                subtitle = "How we protect your data",
                bg = Color(0xFFDDEAFE),
                iconColor = Color(0xFF2563EB),
                icon = Icons.Default.Policy,
                onClick = {}
            )
            MenuButton(
                title = "Data Encryption",
                subtitle = "End-to-end security",
                bg = Color(0xFFEDE9FE),
                iconColor = Color(0xFF7C3AED),
                icon = Icons.Default.Lock,
                trailingText = "Enabled",
                trailingColor = Color(0xFF059669),
                onClick = {}
            )
            MenuButton(
                title = "Export My Data",
                subtitle = "Download all your information",
                bg = Color(0xFFD1FAE5),
                iconColor = Color(0xFF059669),
                icon = Icons.Default.Download,
                onClick = {}
            )
            MenuButton(
                title = "Delete Account",
                subtitle = "Permanently remove your data",
                bg = Color(0xFFFEE2E2),
                iconColor = Color(0xFFDC2626),
                icon = Icons.Default.Delete,
                onClick = {}
            )

            Spacer(Modifier.height(20.dp))

            // LIST OF DATA WE COLLECT
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Data We Collect", fontSize = 16.sp, color = Color.Black)
                    Spacer(Modifier.height(8.dp))
                    Text("• Smoking habits and quit date", color = Color.Gray, fontSize = 13.sp)
                    Text("• Craving logs and patterns", color = Color.Gray, fontSize = 13.sp)
                    Text("• Health metrics (optional)", color = Color.Gray, fontSize = 13.sp)
                    Text("• Usage analytics (anonymized)", color = Color.Gray, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(32.dp)) // extra bottom spacing so last card isn't flush to screen
        }
    }
}

// Reusable Menu Button Composable (like your React buttons)
@Composable
private fun MenuButton(
    title: String,
    subtitle: String,
    bg: Color,
    iconColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailingText: String? = null,
    trailingColor: Color = Color.Gray,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, color = Color.Black)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
            trailingText?.let {
                Text(it, fontSize = 14.sp, color = trailingColor)
            } ?: Text("→", fontSize = 22.sp, color = Color.Gray)
        }
    }
}
