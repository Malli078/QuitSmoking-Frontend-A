@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.quitsmoking.screens.profile

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/* -------------------------
   Helper: decode InputStream → Bitmap
   ------------------------- */
fun loadBitmapFromUri(stream: InputStream?): Bitmap? {
    return try {
        stream.use { s ->
            BitmapFactory.decodeStream(s)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/* -------------------------
   Data models & helpers
   ------------------------- */
data class UserProfile(
    val name: String? = null,
    val email: String? = null,
    val quitDate: String? = null,
    val cigarettesPerDay: Int? = null,
    val costPerPack: Double? = null
)

private data class MenuItem(
    val id: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val route: String,
    val bgColor: Color,
    val iconTint: Color
)

private fun roundSavedMoney(days: Int, cigarettesPerDay: Int, costPerPack: Double): Int {
    val cigarettesAvoided = days * cigarettesPerDay
    val packsAvoided = cigarettesAvoided / 20.0
    return (packsAvoided * costPerPack).toInt()
}

/* -------------------------
   Full ProfileScreen (paste into file)
   ------------------------- */
@Composable
fun ProfileScreen(
    navController: NavController,
    user: UserProfile? = null,
    appVersion: String = "QuitSmart v1.0.0"
) {
    val context = LocalContext.current

    // avatar state
    var avatarBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showChooser by remember { mutableStateOf(false) }

    // logout confirmation dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }

    // camera launcher (declared before use)
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let { avatarBitmap = it }
            showChooser = false
        }

    // gallery launcher
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bmp = loadBitmapFromUri(context.contentResolver.openInputStream(uri))
                if (bmp != null) avatarBitmap = bmp
            }
            showChooser = false
        }

    // permission launcher
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
            if (granted) cameraLauncher.launch(null)
            else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }

    // compute stats
    val daysSinceQuit = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val quitDate = user?.quitDate?.let { sdf.parse(it) }
        if (quitDate != null) {
            val diff = Date().time - quitDate.time
            (diff / (1000L * 60 * 60 * 24)).toInt()
        } else 0
    } catch (_: Exception) { 0 }

    val cigarettesPerDay = user?.cigarettesPerDay ?: 10
    val costPerPack = user?.costPerPack ?: 10.0
    val moneySaved = roundSavedMoney(daysSinceQuit, cigarettesPerDay, costPerPack)

    val menuItems = listOf(
        MenuItem("edit", Icons.Default.Person, "Edit Personal Information", "edit_profile", Color(0xFFDBEAFE), Color(0xFF2563EB)),
        MenuItem("habit", Icons.Default.CalendarMonth, "Habit Settings", "habit_settings", Color(0xFFF3E8FF), Color(0xFF7C3AED)),
        MenuItem("quit-plan", Icons.Default.Settings, "Quit Plan Settings", "quit_plan_settings", Color(0xFFDCFCE7), Color(0xFF059669)),
        MenuItem("notifications", Icons.Default.Notifications, "Notification Settings", "notification_settings", Color(0xFFFFF7ED), Color(0xFFF59E0B)),
        MenuItem("app-settings", Icons.Default.Settings, "App Settings", "app_settings", Color(0xFFF3F4F6), Color(0xFF6B7280)),
        MenuItem("privacy", Icons.Default.Security, "Privacy & Security", "privacy", Color(0xFFECFEFA), Color(0xFF0EA5A3)),
        MenuItem("help", Icons.AutoMirrored.Filled.Help, "Help & Support", "help_support", Color(0xFFEFF6FF), Color(0xFF6366F1)),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF10B981), Color(0xFF059669))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // back button
                IconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column {
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar (image or placeholder) with + badge
                        Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                            if (avatarBitmap != null) {
                                Image(
                                    bitmap = avatarBitmap!!.asImageBitmap(),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color(0xFF059669), modifier = Modifier.size(32.dp))
                                }
                            }

                            // + badge (opens chooser)
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF059669))
                                    .align(Alignment.BottomEnd)
                                    .offset(4.dp, 4.dp)
                                    .clickable { showChooser = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(user?.name ?: "User", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(user?.email ?: "user@example.com", color = Color.White.copy(alpha = 0.95f))
                            }
                        }
                    }
                }
            }

            // Stats card overlapping header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-28).dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$daysSinceQuit", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Text("Days Quit", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("12", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Text("Badges", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$$moneySaved", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Text("Saved", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Menu items: FORCE WHITE background for every card
        items(menuItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { navController.navigate(item.route) },
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // remove heavy shadow
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,           // FORCE white background
                    contentColor = Color(0xFF111827)       // default dark text color
                )
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(item.bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = item.label, tint = item.iconTint)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = item.label, modifier = Modifier.weight(1f), color = Color(0xFF111827))
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color(0xFF9CA3AF))
                }
            }
        }

        // Logout & app version — also white
        item {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        // show confirmation dialog instead of immediate logout
                        showLogoutDialog = true
                    },
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF111827))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFFE9E9)), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color(0xFFDC2626))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Log Out", modifier = Modifier.weight(1f), color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color(0xFFFCA5A5))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                appVersion,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Image chooser dialog
    if (showChooser) {
        AlertDialog(
            onDismissRequest = { showChooser = false },
            confirmButton = {
                TextButton(onClick = { galleryLauncher.launch("image/*") }) { Text("Gallery") }
            },
            dismissButton = {
                TextButton(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Camera") }
            },
            title = { Text("Set profile photo") },
            text = { Text("Choose from gallery or take a picture.") }
        )
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    // perform logout: navigate to login and clear back stack
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }) {
                    Text("Log Out", color = Color(0xFFDC2626))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


