package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.ui.SelectedDate
import com.example.util.HijriCalendarUtil
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    initialDate: SelectedDate,
    taskToEdit: Task?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        priority: String,
        repeat: String,
        reminder: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var year by remember { mutableIntStateOf(taskToEdit?.year ?: initialDate.year) }
    var month by remember { mutableIntStateOf(taskToEdit?.month ?: initialDate.month) }
    var day by remember { mutableIntStateOf(taskToEdit?.day ?: initialDate.day) }
    var hour by remember { mutableIntStateOf(taskToEdit?.hour ?: 9) }
    var minute by remember { mutableIntStateOf(taskToEdit?.minute ?: 0) }
    var priority by remember { mutableStateOf(taskToEdit?.priority ?: "মাঝারি") }
    var repeat by remember { mutableStateOf(taskToEdit?.repeat ?: "একবার") }
    var reminder by remember { mutableStateOf(taskToEdit?.reminder ?: false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val priorities = listOf("জরুরি", "উচ্চ", "মাঝারি", "কম")
    val repeats = listOf("একবার", "প্রতিদিন", "প্রতি সপ্তাহে", "প্রতি মাসে")

    var priorityExpanded by remember { mutableStateOf(false) }
    var repeatExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (taskToEdit == null) "নতুন কাজ" else "কাজ সম্পাদনা",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("কাজের নাম") },
                    placeholder = { Text("কাজের বিবরণ লিখুন...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Date Picker Tile
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .testTag("task_date_picker_button")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "তারিখ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${HijriCalendarUtil.toBengaliDigits(day)}/${HijriCalendarUtil.toBengaliDigits(month)}/${HijriCalendarUtil.toBengaliDigits(year)}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }

                // Time Picker Tile
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true }
                        .testTag("task_time_picker_button")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "সময়",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = HijriCalendarUtil.formatTime(hour, minute),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }

                // Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("অগ্রাধিকার") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorities.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    priority = option
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Repeat Dropdown
                ExposedDropdownMenuBox(
                    expanded = repeatExpanded,
                    onExpandedChange = { repeatExpanded = !repeatExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = repeat,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("পুনরাবৃত্তি") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = repeatExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = repeatExpanded,
                        onDismissRequest = { repeatExpanded = false }
                    ) {
                        repeats.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    repeat = option
                                    repeatExpanded = false
                                }
                            )
                        }
                    }
                }

                // Reminder Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "রিমাইন্ডার চালু",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = reminder,
                        onCheckedChange = { reminder = it },
                        modifier = Modifier.testTag("reminder_switch")
                    )
                }

                if (reminder) {
                    Text(
                        text = "নোটিফিকেশন ইঞ্জিন পরবর্তী Android build-এ যুক্ত হবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, year, month, day, hour, minute, priority, repeat, reminder)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("task_save_button")
            ) {
                Text("সংরক্ষণ")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("task_cancel_button")
            ) {
                Text("বাতিল")
            }
        }
    )

    // Android M3 DatePicker Modal
    if (showDatePicker) {
        val initialCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialCal.timeInMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val cal = Calendar.getInstance().apply { timeInMillis = millis }
                            year = cal.get(Calendar.YEAR)
                            month = cal.get(Calendar.MONTH) + 1
                            day = cal.get(Calendar.DAY_OF_MONTH)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("ঠিক আছে")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("বাতিল")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Android M3 TimePicker Modal
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("সময় নির্বাচন করুন") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        hour = timePickerState.hour
                        minute = timePickerState.minute
                        showTimePicker = false
                    }
                ) {
                    Text("ঠিক আছে")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
