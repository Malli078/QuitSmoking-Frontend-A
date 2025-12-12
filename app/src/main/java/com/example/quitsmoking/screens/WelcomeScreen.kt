package com.example.quitsmoking.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WelcomeScreen(navController: NavController) {

    val features = listOf(
        Triple(Icons.Filled.Favorite, "Track your health recovery in real-time", Color(0xFFE53935)),
        Triple(Icons.Filled.TrendingUp, "Beat cravings with AI support", Color(0xFF1E88E5)),
        Triple(Icons.Filled.AutoAwesomeMotion, "Earn badges and celebrate milestones", Color(0xFFFFC107)),
        Triple(Icons.Filled.AutoAwesome, "Get personalized quit strategies", Color(0xFF8E24AA)),
    )

    // header gradient and fixed height
    val headerGradient = Brush.verticalGradient(
        listOf(Color(0xFF00897B), Color(0xFF00ACC1))
    )
    val headerHeight = 260.dp // adjust as needed for your desired spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE0F2F1), Color(0xFFE0F7FA))))
    ) {

        // ------------ HEADER (fixed height) with Skip pinned top-right ------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(headerGradient)
                .padding(horizontal = 16.dp)
        ) {
            // Skip button pinned to top-right (inside header area)
            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp)
            ) {
                Text("Skip", color = Color.White, fontSize = 14.sp)
            }

            // Centered icon + text block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color(0xFF00897B),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .widthIn(max = 340.dp)
                        .fillMaxWidth(0.9f)
                        .padding(start = 12.dp) // slight nudge right
                ) {
                    Text(
                        "Welcome to QuitSmart",
                        color = Color.White,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Your personal companion for a smoke-free life",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Add a small spacer so the rounded-corner look has breathing room (optional)
        Spacer(modifier = Modifier.height(8.dp))

        // ------------ FEATURE CARDS (will always start below header) ------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            features.forEach {
                FeatureCard(
                    icon = it.first,
                    text = it.second,
                    tint = it.third
                )
            }
        }

        // ------------ ACTION BUTTONS ------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = { navController.navigate("why_quit") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Get Started", color = Color.White, fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text(
                    "Already Have an Account? Log In",
                    color = Color(0xFF00897B),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun FeatureCard(icon: Any, text: String, tint: Color) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(text, color = Color(0xFF424242), fontSize = 15.sp)
        }
    }
}
