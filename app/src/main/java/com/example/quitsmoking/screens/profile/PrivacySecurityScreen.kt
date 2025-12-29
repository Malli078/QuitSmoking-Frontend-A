@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.quitsmoking.screens.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quitsmoking.viewmodel.PrivacySecurityViewModel

@Composable
fun PrivacySecurityScreen(
    navController: NavController,
    viewModel: PrivacySecurityViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    val userId = prefs.getInt("user_id", 0)

    val loading by viewModel.loading.collectAsState()
    val message by viewModel.message.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    // MESSAGE TOAST
    LaunchedEffect(message) {
        message?.let {
            android.widget.Toast
                .makeText(context, it, android.widget.Toast.LENGTH_SHORT)
                .show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Privacy & Security") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
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

            Text("Your data is protected", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))

            // INFO CARD
            InfoCard()

            Spacer(Modifier.height(20.dp))

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
                onClick = { viewModel.exportData(userId) }
            )

            MenuButton(
                title = "Delete Account",
                subtitle = "Permanently remove your data",
                bg = Color(0xFFFEE2E2),
                iconColor = Color(0xFFDC2626),
                icon = Icons.Default.Delete,
                onClick = { showDeleteDialog = true }
            )
        }
    }

    // DELETE CONFIRMATION
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This action is permanent. Are you sure?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAccount(userId) {
                        prefs.edit().clear().apply()
                        navController.navigate("login") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

/* ---------- SMALL UI HELPERS ---------- */

@Composable
private fun InfoCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F8EE)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF34D399).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Security, null, tint = Color(0xFF059669))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Your Privacy Matters", fontSize = 16.sp)
                Text(
                    "All your data is encrypted and stored securely.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

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
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
            trailingText?.let {
                Text(it, color = trailingColor)
            } ?: Text("→", fontSize = 22.sp, color = Color.Gray)
        }
    }
}
