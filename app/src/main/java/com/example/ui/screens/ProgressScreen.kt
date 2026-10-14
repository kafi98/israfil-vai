package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.PriorityUrgent
import com.example.util.HijriCalendarUtil
import kotlin.math.roundToInt

@Composable
fun ProgressScreen(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    val totalCount = tasks.size
    val doneCount = tasks.count { it.done }
    val remainingCount = totalCount - doneCount
    val rawProgress = if (totalCount == 0) 0f else doneCount.toFloat() / totalCount.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    val percentage = (rawProgress * 100).roundToInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("progress_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Text(
                text = "অগ্রগতি",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Circular Gauge Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Track background circle
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(170.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 14.dp,
                            strokeCap = StrokeCap.Round
                        )

                        // Active progress circle
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(170.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 14.dp,
                            strokeCap = StrokeCap.Round
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${HijriCalendarUtil.toBengaliDigits(percentage)}%",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 34.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "সম্পন্ন",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "${HijriCalendarUtil.toBengaliDigits(doneCount)}টি সম্পন্ন  •  ${HijriCalendarUtil.toBengaliDigits(remainingCount)}টি বাকি",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Priority Breakdown Section
        item {
            Text(
                text = "অগ্রাধিকার ভিত্তিক বিভাজন",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val priorities = listOf(
            Triple("জরুরি", PriorityUrgent, tasks.filter { it.priority == "জরুরি" }),
            Triple("উচ্চ", PriorityHigh, tasks.filter { it.priority == "উচ্চ" }),
            Triple("মাঝারি", PriorityMedium, tasks.filter { it.priority == "মাঝারি" }),
            Triple("কম", PriorityLow, tasks.filter { it.priority == "কম" })
        )

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    priorities.forEach { (label, color, pTasks) ->
                        val pTotal = pTasks.size
                        val pDone = pTasks.count { it.done }
                        val pProgress = if (pTotal == 0) 0f else pDone.toFloat() / pTotal.toFloat()

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Text(
                                    text = "${HijriCalendarUtil.toBengaliDigits(pDone)}/${HijriCalendarUtil.toBengaliDigits(pTotal)} (${(pProgress * 100).roundToInt()}%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { pProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = color,
                                trackColor = color.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }
        }
    }
}
