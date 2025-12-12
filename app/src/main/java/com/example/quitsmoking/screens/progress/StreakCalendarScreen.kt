package com.example.quitsmoking.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.max

@Composable
fun StreakCalendarScreen(
    navController: NavController,
    userQuitDateMillis: Long? = null
) {
    val quitDate = remember(userQuitDateMillis) {
        if (userQuitDateMillis != null) {
            LocalDate.ofEpochDay((userQuitDateMillis / 86_400_000L).coerceAtLeast(0L))
        } else {
            LocalDate.now()
        }
    }

    val today = remember { LocalDate.now() }
    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonthIndex by remember { mutableStateOf(today.monthValue - 1) } // 0-based
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val daysSinceQuit = remember(quitDate) {
        max(0, ChronoUnit.DAYS.between(quitDate, today).toInt())
    }

    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val yearMonth = remember(currentYear, currentMonthIndex) {
        YearMonth.of(currentYear, currentMonthIndex + 1)
    }
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val jsStarting = firstDay.dayOfWeek.value % 7 // map SUN->0, MON->1, ...

    val calendarDays = remember(currentYear, currentMonthIndex) {
        val list = mutableStateListOf<Int?>()
        repeat(jsStarting) { list.add(null) } // leading blanks
        for (d in 1..daysInMonth) list.add(d)
        list
    }

    fun isDaySmokeFree(day: Int): Boolean {
        val check = LocalDate.of(currentYear, currentMonthIndex + 1, day)
        return !check.isBefore(quitDate) && !check.isAfter(today)
    }

    fun isTodayDate(day: Int): Boolean {
        return day == today.dayOfMonth && currentMonthIndex == (today.monthValue - 1) && currentYear == today.year
    }

    fun isSelectedDate(day: Int): Boolean {
        val sel = selectedDate ?: return false
        return sel.dayOfMonth == day && sel.monthValue == currentMonthIndex + 1 && sel.year == currentYear
    }

    fun handleDateClick(day: Int) {
        selectedDate = LocalDate.of(currentYear, currentMonthIndex + 1, day)
    }

    fun getDaysSinceQuitForDate(date: LocalDate): Int {
        return max(0, ChronoUnit.DAYS.between(quitDate, date).toInt())
    }

    fun goToPreviousMonth() {
        if (currentMonthIndex == 0) {
            currentMonthIndex = 11
            currentYear -= 1
        } else {
            currentMonthIndex -= 1
        }
        selectedDate = null
    }

    fun goToNextMonth() {
        if (currentYear == today.year && currentMonthIndex == (today.monthValue - 1)) return
        if (currentMonthIndex == 11) {
            currentMonthIndex = 0
            currentYear += 1
        } else {
            currentMonthIndex += 1
        }
        selectedDate = null
    }

    val canGoNext = !(currentYear == today.year && currentMonthIndex == (today.monthValue - 1))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444)))
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .clickable { navController.navigate("home") }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Streak Calendar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Your smoke-free journey", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // Current Streak card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Current Streak", color = Color.Gray, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "$daysSinceQuit", fontSize = 34.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "days", color = Color.Gray)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick access buttons (3 columns)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                @Composable
                fun QuickBtn(label: String, emoji: String, onClick: () -> Unit) {
                    // Put clickable on the top-level Surface to ensure the whole tile is tappable
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onClick() },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        tonalElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF7ED)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(label, fontSize = 12.sp, color = Color(0xFF111827), textAlign = TextAlign.Center)
                        }
                    }
                }

                // Achievements button now navigates to the achievements route
                QuickBtn("Achievements", "🏆") { navController.navigate("achievements") }

                // Other example buttons
                QuickBtn("Money Saved", "💵") { navController.navigate("money-saved") }
                QuickBtn("Milestones", "🎖️") { navController.navigate("milestone-celebration") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { goToPreviousMonth() }) {
                            Text("◀", fontSize = 16.sp, color = Color(0xFF374151))
                        }
                        Text(text = "${monthNames[currentMonthIndex]} $currentYear", fontSize = 16.sp, color = Color(0xFF111827), fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = { goToNextMonth() }, enabled = canGoNext) {
                            Text("▶", fontSize = 16.sp, color = if (canGoNext) Color(0xFF374151) else Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Days of week header
                    Row(modifier = Modifier.fillMaxWidth()) {
                        daysOfWeek.forEach { d ->
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                Text(text = d, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar grid (rows of 7)
                    val rows = calendarDays.chunked(7)
                    rows.forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            week.forEach { day ->
                                Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                                    if (day == null) {
                                        Spacer(modifier = Modifier.fillMaxSize())
                                    } else {
                                        val smokeFree = isDaySmokeFree(day)
                                        val todayCell = isTodayDate(day)
                                        val selected = isSelectedDate(day)
                                        val bgModifier = if (todayCell) {
                                            Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444))))
                                        } else {
                                            Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (selected) Color(0xFFEFF6FF) else if (smokeFree) Color(0xFFECFDF5) else Color(0xFFF3F4F6))
                                        }

                                        Box(modifier = bgModifier.clickable { handleDateClick(day) }, contentAlignment = Alignment.Center) {
                                            Text(
                                                text = day.toString(),
                                                color = when {
                                                    todayCell -> Color.White
                                                    smokeFree -> Color(0xFF065F46)
                                                    else -> Color(0xFF9CA3AF)
                                                },
                                                fontSize = 14.sp
                                            )
                                            if (smokeFree && !todayCell) {
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(4.dp)
                                                        .size(12.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF10B981)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("✓", color = Color.White, fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFECFDF5)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Smoke-free", fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFF3F4F6)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Before quit", fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(
                                Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFEF4444))).let { Color.Unspecified }
                            ))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Today", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selected date details
            selectedDate?.let { sel ->
                val selDaysSince = getDaysSinceQuitForDate(sel)
                Surface(
                    color = Color.Unspecified,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Selected Date", color = Color.White, fontSize = 16.sp)
                                Text("✕", color = Color.White.copy(alpha = 0.85f), modifier = Modifier.clickable { selectedDate = null })
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = sel.toString(), color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            when {
                                !sel.isBefore(quitDate) && !sel.isAfter(today) -> {
                                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.10f), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("Days smoke-free on this date", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("$selDaysSince ${if (selDaysSince == 1) "day" else "days"}", color = Color.White, fontSize = 20.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("✓", color = Color(0xFF10B981))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Smoke-free day! 🎉", color = Color.White)
                                    }
                                    if (sel.isEqual(quitDate)) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.10f), modifier = Modifier.fillMaxWidth()) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text("🎊 This is your quit date! The beginning of your amazing journey!", color = Color.White, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                                sel.isBefore(quitDate) -> {
                                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.10f), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("This date was before you quit smoking.", color = Color.White, fontSize = 13.sp)
                                        }
                                    }
                                }
                                else -> {
                                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.10f), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("This date is in the future. Keep up the great work!", color = Color.White, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Stats grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(modifier = Modifier.weight(1f), color = Color.White, shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Longest Streak", color = Color.Gray, fontSize = 12.sp)
                        Text("$daysSinceQuit days", color = Color(0xFF111827), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(modifier = Modifier.weight(1f), color = Color.White, shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Days", color = Color.Gray, fontSize = 12.sp)
                        Text("$daysSinceQuit", color = Color(0xFF111827), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Motivation card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.Unspecified
            ) {
                Box(modifier = Modifier.background(Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899)))).padding(12.dp)) {
                    Column {
                        Text("Keep it up!", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        val message = when {
                            daysSinceQuit == 0 -> "Today is day one. You've got this!"
                            daysSinceQuit < 7 -> "Just ${7 - daysSinceQuit} more days to one week!"
                            daysSinceQuit < 30 -> "${30 - daysSinceQuit} days until one month milestone!"
                            daysSinceQuit < 90 -> "${90 - daysSinceQuit} days until 3 months smoke-free!"
                            else -> "You're doing amazing! Keep going! 🎉"
                        }
                        Text(message, color = Color.White, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Next achievement card (clickable -> achievements)
            Spacer(modifier = Modifier.height(8.dp))
            // Make sure clickable is applied to the full-width surface (or top Box) so the tap registers
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("achievements") },
                shape = RoundedCornerShape(16.dp),
                color = Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899))).let { Color.Unspecified }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Achievements", color = Color.White.copy(alpha = 0.95f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Tap to view all badges", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
