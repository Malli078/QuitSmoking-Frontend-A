package com.example.quitsmoking.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateAccountScreen(navController: NavController) {

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var quitDate by remember { mutableStateOf("") } // editable by keyboard now

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }
    var triedSubmit by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // DatePicker — when user picks, we set quitDate
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                quitDate = dateFormat.format(calendar.time)
                dateError = ""
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Validate whether quitDate is a valid yyyy-MM-dd date
    fun isQuitDateValid(text: String): Boolean {
        return try {
            if (text.isBlank()) return false
            dateFormat.isLenient = false
            dateFormat.parse(text)
            true
        } catch (e: Exception) {
            false
        }
    }

    // validation function
    fun validateFields(): Boolean {
        var ok = true

        if (name.text.isBlank()) {
            nameError = "Please enter your name"
            ok = false
        } else nameError = ""

        val emailText = email.text.trim()
        if (emailText.isBlank()) {
            emailError = "Please enter your email"
            ok = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailError = "Invalid email address"
            ok = false
        } else emailError = ""

        if (!isQuitDateValid(quitDate)) {
            dateError = "Enter date as yyyy-MM-dd (or use calendar)"
            ok = false
        } else dateError = ""

        return ok
    }

    // form valid check
    val isFormValid = name.text.isNotBlank() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.trim()).matches() &&
            isQuitDateValid(quitDate)

    // animated button color / elevation
    val animatedButtonColor by animateColorAsState(
        targetValue = if (isFormValid) Color(0xFF00A9A5) else Color(0xFFBDBDBD),
        animationSpec = tween(300)
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isFormValid) 10.dp else 0.dp,
        animationSpec = tween(300)
    )

    val headerBrush = Brush.horizontalGradient(listOf(Color(0xFFE8F6F6), Color(0xFFDFF2F1)))

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(headerBrush),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // avatar placeholder (initials)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00897B)),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = name.text
                            .trim()
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2)
                            .joinToString("")
                            .uppercase(Locale.getDefault())
                        Text(
                            text = if (initials.isBlank()) "QS" else initials,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Create your account",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Personalize your quit journey",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = elevatedCardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (triedSubmit) nameError = ""
                        },
                        label = { Text("Full name") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF008577)) },
                        isError = nameError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { if (nameError.isNotEmpty()) Text(nameError, color = Color.Red) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (nameError.isNotEmpty()) Color.Red else Color(0xFF008577),
                            unfocusedBorderColor = if (nameError.isNotEmpty()) Color.Red else Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (triedSubmit) emailError = ""
                        },
                        label = { Text("Email address") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF008577)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = emailError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (emailError.isNotEmpty()) Text(emailError, color = Color.Red)
                            else Text("We'll send helpful reminders to this email.", color = Color.Gray)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (emailError.isNotEmpty()) Color.Red else Color(0xFF008577),
                            unfocusedBorderColor = if (emailError.isNotEmpty()) Color.Red else Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quit date — editable field (keyboard opens). trailing icon opens DatePicker.
                    OutlinedTextField(
                        value = quitDate,
                        onValueChange = {
                            quitDate = it
                            if (triedSubmit) {
                                dateError = ""
                            }
                        },
                        label = { Text("Quit date (yyyy-MM-dd)") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF008577))
                        },
                        trailingIcon = {
                            IconButton(onClick = { datePicker.show() }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Open calendar", tint = Color(0xFF008577))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = dateError.isNotEmpty(),
                        supportingText = {
                            if (dateError.isNotEmpty()) {
                                Text(dateError, color = Color.Red)
                            } else {
                                Text(if (quitDate.isNotBlank()) "Selected: $quitDate" else "Type or pick a date", color = Color.Gray)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (dateError.isNotEmpty()) Color.Red else Color(0xFF008577),
                            unfocusedBorderColor = if (dateError.isNotEmpty()) Color.Red else Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Benefits
                    Text("Why create an account", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• AI-powered craving predictions", color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Personalized health tracking", color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Daily motivation & support", color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        triedSubmit = true
                        if (validateFields()) {
                            navController.navigate("home") {
                                popUpTo("create_account") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = animatedButtonColor),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = animatedElevation)
                ) {
                    Text(text = if (isFormValid) "Start my journey" else "Complete details", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
        }
    }
}
