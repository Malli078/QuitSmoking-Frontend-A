// src/main/java/com/example/quitsmoking/screens/health/RecoveryGraphScreen.kt
package com.example.quitsmoking.screens.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random
import java.time.Instant
import java.time.Duration

// material icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

private data class DayPoint(val day: Int, val lung: Float, val heart: Float, val energy: Float)

@Composable
fun RecoveryGraphScreen(navController: NavController, quitDateIso: String?) {
    // compute days since quit using quitDateIso (ISO string) - fallback to 0
    val daysSinceQuit = remember(quitDateIso) {
        val now = Instant.now()
        val quitInstant = try {
            if (quitDateIso == null) now else Instant.parse(quitDateIso)
        } catch (e: Exception) {
            try {
                Instant.ofEpochMilli(quitDateIso?.toLong() ?: now.toEpochMilli())
            } catch (_: Exception) {
                now
            }
        }
        val days = Duration.between(quitInstant, now).toDays().coerceAtLeast(0)
        days.toInt()
    }

    // Generate up-to-30 days worth of data (deterministic-ish by index)
    val graphData: List<DayPoint> = remember(daysSinceQuit) {
        val size = min(daysSinceQuit + 1, 30).coerceAtLeast(1)
        (0 until size).map { i ->
            val rand = Random(i)
            val jitter = { amp: Float -> (rand.nextFloat() - 0.3f) * amp }
            DayPoint(
                day = i + 1,
                lung = min(100f, 40f + (i * 2f) + jitter(5f)),
                heart = min(100f, 50f + (i * 1.8f) + jitter(4f)),
                energy = min(100f, 35f + (i * 2.2f) + jitter(6f))
            )
        }
    }

    val latest = graphData.last()
    val lungCurrent = latest.lung.roundToIntSafe()
    val heartCurrent = latest.heart.roundToIntSafe()
    val energyCurrent = latest.energy.roundToIntSafe()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // Header gradient with back button + title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF7C3AED), Color(0xFFF472B6))
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(40.dp)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Column(
                modifier = Modifier
                    .padding(start = 56.dp) // leave space for the back button
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Recovery Graph",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Your health is improving!",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Card with chart
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("30-Day Recovery Trends", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${graphData.size} day(s) shown", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF10B981), shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("↑", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Chart area via Canvas
                    val chartHeightDp = 180.dp
                    val chartPadding = 8.dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(chartHeightDp)
                    ) {
                        val paddingPx = with(LocalDensity.current) { chartPadding.toPx() }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (graphData.size <= 1) {
                                // placeholder background
                                drawRect(color = Color(0xFFF1F5F9))
                                return@Canvas
                            }

                            val w = size.width
                            val h = size.height
                            val left = paddingPx
                            val right = w - paddingPx
                            val top = paddingPx
                            val bottom = h - paddingPx

                            val plotWidth = right - left
                            val plotHeight = bottom - top

                            val n = graphData.size
                            val xForIndex = { idx: Int ->
                                left + (plotWidth * (idx.toFloat() / (n - 1).coerceAtLeast(1)))
                            }
                            val yForValue = { value: Float ->
                                bottom - (plotHeight * (value.coerceIn(0f, 100f) / 100f))
                            }

                            fun buildSeriesPath(values: List<Float>): Path {
                                val path = Path()
                                values.forEachIndexed { idx, v ->
                                    val x = xForIndex(idx)
                                    val y = yForValue(v)
                                    if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }
                                return path
                            }

                            val lungVals = graphData.map { it.lung }
                            val heartVals = graphData.map { it.heart }
                            val energyVals = graphData.map { it.energy }

                            val lungPath = buildSeriesPath(lungVals)
                            val heartPath = buildSeriesPath(heartVals)
                            val energyPath = buildSeriesPath(energyVals)

                            fun closedArea(path: Path, values: List<Float>): Path {
                                val p = Path().apply { addPath(path) }
                                val lastX = xForIndex(values.lastIndex)
                                val firstX = xForIndex(0)
                                p.lineTo(lastX, bottom)
                                p.lineTo(firstX, bottom)
                                p.close()
                                return p
                            }

                            // Draw filled areas (light gradients)
                            drawPath(path = closedArea(energyPath, energyVals), brush = Brush.verticalGradient(listOf(Color(0xFFFDE68A), Color.Transparent)), style = Fill)
                            drawPath(path = closedArea(heartPath, heartVals), brush = Brush.verticalGradient(listOf(Color(0xFFFCA5A5), Color.Transparent)), style = Fill)
                            drawPath(path = closedArea(lungPath, lungVals), brush = Brush.verticalGradient(listOf(Color(0xFFBAF3FF), Color.Transparent)), style = Fill)

                            // Draw strokes for each series
                            val stroke = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            drawPath(lungPath, color = Color(0xFF06B6D4), style = stroke)
                            drawPath(heartPath, color = Color(0xFFF43F5E), style = stroke)
                            drawPath(energyPath, color = Color(0xFFF59E0B), style = stroke)

                            // light horizontal grid lines
                            val gridColor = Color(0xFFF1F5F9)
                            for (v in 0..4) {
                                val y = top + (plotHeight * (v.toFloat() / 4f))
                                drawLine(color = gridColor, start = Offset(left, y), end = Offset(right, y), strokeWidth = 1f)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        LegendDot(label = "Lung", color = Color(0xFF06B6D4))
                        Spacer(modifier = Modifier.width(12.dp))
                        LegendDot(label = "Heart", color = Color(0xFFF43F5E))
                        Spacer(modifier = Modifier.width(12.dp))
                        LegendDot(label = "Energy", color = Color(0xFFF59E0B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current Stats row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(modifier = Modifier.weight(1f), label = "Lung Function", value = "$lungCurrent%")
                StatCard(modifier = Modifier.weight(1f), label = "Heart Health", value = "$heartCurrent%")
                StatCard(modifier = Modifier.weight(1f), label = "Energy", value = "$energyCurrent%")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Insights card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Keep Going!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Your body is recovering faster than you might think. Every day brings new improvements.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        InsightLine("Lung capacity improves by 10% within first month")
                        InsightLine("Circulation normalizes within 2-12 weeks")
                        InsightLine("Energy levels increase significantly by week 4")
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("•", color = Color(0xFF06B6D4))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InsightLine(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("•", color = Color(0xFF7C3AED))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
    }
}

// helpers
private fun Float.roundToIntSafe(): Int = round(this).toInt()
private fun Double.roundToIntSafe(): Int = round(this).toInt()
