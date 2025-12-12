package com.example.quitsmoking.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF00897B)
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = "Enter your email to receive reset link",
            color = Color.Gray
        )
        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("forgot_password") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
        ) {
            Text("Send Reset Link", color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Back to Login", color = Color(0xFF00897B))
        }
    }
}
