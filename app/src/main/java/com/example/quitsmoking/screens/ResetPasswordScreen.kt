package com.example.quitsmoking.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quitsmoking.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    email: String
) {
    val vm: ResetPasswordViewModel = viewModel()
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ✅ After password reset → Login
    LaunchedEffect(vm.success.value) {
        if (vm.success.value) {
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(60.dp))

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (vm.error.value.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(vm.error.value, color = Color.Red)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                vm.reset(email, otp, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !vm.loading.value
        ) {
            if (vm.loading.value) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp),
                    color = Color.White
                )
            } else {
                Text("Reset Password")
            }
        }
    }
}
