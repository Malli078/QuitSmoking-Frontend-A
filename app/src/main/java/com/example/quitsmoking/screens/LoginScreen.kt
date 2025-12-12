package com.example.quitsmoking.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    // NEW: error message state
    var errorMessage by remember { mutableStateOf("") }
    var triedLogin by remember { mutableStateOf(false) }

    // Validation logic
    fun validate(): Boolean {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
            return false
        }
        if (!email.contains("@") || !email.contains(".")) {
            errorMessage = "Invalid email format"
            return false
        }
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters"
            return false
        }
        return true
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3FEFE))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "QuitSmoking",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF00695C)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Welcome Back 👋",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF00695C)
            )
            Text(
                "Login to continue your quit journey",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (triedLogin) errorMessage = ""
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (triedLogin) errorMessage = ""
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation()
                    )

                    // 🔴 ERROR MESSAGE (only shows when wrong input)
                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = { navController.navigate("forgot_password") },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text("Forgot Password?", color = Color(0xFF00897B))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // LOGIN BUTTON
            Button(
                onClick = {
                    triedLogin = true
                    if (validate()) {
                        // If valid → go to home
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00897B)
                )
            ) {
                Text("Log In", color = Color.White, fontSize = 17.sp)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = Color.Gray)

                TextButton(onClick = {
                    navController.navigate("create_account")
                }) {
                    Text("Sign Up", color = Color(0xFF00897B))
                }
            }
        }
    }
}
