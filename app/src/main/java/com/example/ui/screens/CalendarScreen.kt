package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.ui.SelectedDate
import com.example.ui.components.TaskCard
import com.example.util.HijriCalendarUtil
import java.util.Calendar

@Composable
fun CalendarScreen(
    calendarMonth: Pair<Int, Int>, // (year, month 1-12)
    selectedDate: SelectedDate,
    tasks: List<Task>,
    onSelectDate: (year: Int, month: Int, day: Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToggleTaskDone: (Task, Boolean) -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val (year, month) = calendarMonth

    val cal = remember(year, month) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val daysInMonth = remember(year, month) {
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Monday-first offset
    val startOffset = remember(year, month) {
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        if (dow == Calendar.SUNDAY) 6 else dow - 2
    }

    val selectedDayTasks = remember(tasks, selectedDate) {
        tasks.filter {
            it.year == selectedDate.year &&
            it.month == selectedDate.month &&
            it.day == selectedDate.day
        }.sortedWith(compareBy({ it.hour }, { it.minute }))
    }

    val monthName = HijriCalendarUtil.BENGALI_MONTHS[(month - 1).coerceIn(0, 11)]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Header Navigation
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPrevMonth,
                        modifier = Modifier.testTag("calendar_prev_month")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "পূর্ববর্তী মাস"
                        )
                    }

                    Text(
                        text = "$monthName ${HijriCalendarUtil.toBengaliDigits(year)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onNextMonth,
                        modifier = Modifier.testTag("calendar_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "পরবর্তী মাস"
                        )
                    }
                }
            }
        }

        // Calendar Grid Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Weekdays Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HijriCalendarUtil.BENGALI_WEEKDAYS.forEach { dayName ->
                            Text(
                                text = dayName,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (dayName == "শুক্র") {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Days Grid
                    val totalCells = startOffset + daysInMonth
                    val totalRows = (totalCells + 6) / 7

                    for (row in 0 until totalRows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                val dayNum = cellIndex - startOffset + 1

                                if (dayNum in 1..daysInMonth) {
                                    val isSelected = selectedDate.year == year &&
                                            selectedDate.month == month &&
                                            selectedDate.day == dayNum

                                    val tasksCount = tasks.count {
                                        it.year == year && it.month == month && it.day == dayNum
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                onSelectDate(year, month, dayNum)
                                            }
                                            .testTag("calendar_day_$dayNum"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier.fillMaxSize(),
                                            shape = RoundedCornerShape(12.dp),
                                            color = when {
                                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                                tasksCount > 0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                else -> Color.Transparent
                                            },
                                            border = if (isSelected) {
                                                BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                            } else null
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = HijriCalendarUtil.toBengaliDigits(dayNum),
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    ),
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    }
                                                )

                                                if (tasksCount > 0) {
                                                    val dots = "•".repeat(tasksCount.coerceAtMost(3))
                                                    Text(
                                                        text = dots,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.ExtraBold
                                                        ),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        lineHeight = 8.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Date Info Line
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = HijriCalendarUtil.formatDateLine(
                        selectedDate.year,
                        selectedDate.month,
                        selectedDate.day
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Tasks for selected date
        if (selectedDayTasks.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "এই দিনে কোনো কাজ নেই।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(
                items = selectedDayTasks,
                key = { it.id }
            ) { task ->
                TaskCard(
                    task = task,
                    onToggleDone = { onToggleTaskDone(task, it) },
                    onEdit = { onEditTask(task) },
                    onDelete = { onDeleteTask(task) }
                )
            }
        }
    }
}
