package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.TaskDialog
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlanScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val monthlyGoal by viewModel.monthlyGoal.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val calendarMonth by viewModel.calendarMonth.collectAsStateWithLifecycle()
    val isTaskDialogOpen by viewModel.isTaskDialogOpen.collectAsStateWithLifecycle()
    val editingTask by viewModel.editingTask.collectAsStateWithLifecycle()
    val isDarkModeState by viewModel.isDarkMode.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "আমার পরিকল্পনা",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.jumpToToday() },
                        modifier = Modifier.testTag("jump_to_today_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "আজকের দিনে যান",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // Home
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "হোম"
                        )
                    },
                    label = { Text("হোম") },
                    modifier = Modifier.testTag("nav_item_home")
                )

                // Calendar
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 1) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "ক্যালেন্ডার"
                        )
                    },
                    label = { Text("ক্যালেন্ডার") },
                    modifier = Modifier.testTag("nav_item_calendar")
                )

                // Plan
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 2) Icons.Filled.EditCalendar else Icons.Outlined.EditCalendar,
                            contentDescription = "পরিকল্পনা"
                        )
                    },
                    label = { Text("পরিকল্পনা") },
                    modifier = Modifier.testTag("nav_item_plan")
                )

                // Progress
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 3) Icons.Filled.Insights else Icons.Outlined.Insights,
                            contentDescription = "অগ্রগতি"
                        )
                    },
                    label = { Text("অগ্রগতি") },
                    modifier = Modifier.testTag("nav_item_progress")
                )

                // Settings
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { viewModel.setTab(4) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 4) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "সেটিংস"
                        )
                    },
                    label = { Text("সেটিংস") },
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        },
        floatingActionButton = {
            if (currentTab == 0 || currentTab == 2) {
                FloatingActionButton(
                    onClick = { viewModel.openNewTaskDialog() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_add_task")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "নতুন কাজ যোগ করুন"
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "tab_transition"
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    selectedDate = selectedDate,
                    tasks = tasks,
                    monthlyGoal = monthlyGoal,
                    onUpdateMonthlyGoal = { viewModel.updateMonthlyGoal(it) },
                    onOpenAddTask = { viewModel.openNewTaskDialog() },
                    onToggleTaskDone = { task, done -> viewModel.toggleTaskDone(task, done) },
                    onEditTask = { task -> viewModel.openEditTaskDialog(task) },
                    onDeleteTask = { task -> viewModel.deleteTask(task) }
                )

                1 -> CalendarScreen(
                    calendarMonth = calendarMonth,
                    selectedDate = selectedDate,
                    tasks = tasks,
                    onSelectDate = { y, m, d -> viewModel.setSelectedDate(y, m, d) },
                    onPrevMonth = { viewModel.shiftCalendarMonth(-1) },
                    onNextMonth = { viewModel.shiftCalendarMonth(1) },
                    onToggleTaskDone = { task, done -> viewModel.toggleTaskDone(task, done) },
                    onEditTask = { task -> viewModel.openEditTaskDialog(task) },
                    onDeleteTask = { task -> viewModel.deleteTask(task) }
                )

                2 -> PlanScreen(
                    tasks = tasks,
                    monthlyGoal = monthlyGoal,
                    onOpenAddTask = { viewModel.openNewTaskDialog() },
                    onToggleTaskDone = { task, done -> viewModel.toggleTaskDone(task, done) },
                    onEditTask = { task -> viewModel.openEditTaskDialog(task) },
                    onDeleteTask = { task -> viewModel.deleteTask(task) }
                )

                3 -> ProgressScreen(
                    tasks = tasks
                )

                4 -> SettingsScreen(
                    isDarkMode = isDarkModeState ?: false,
                    onToggleDarkMode = { viewModel.setDarkMode(it) },
                    tasks = tasks,
                    monthlyGoal = monthlyGoal
                )
            }
        }

        // Add / Edit Task Dialog
        if (isTaskDialogOpen) {
            TaskDialog(
                initialDate = selectedDate,
                taskToEdit = editingTask,
                onDismiss = { viewModel.closeTaskDialog() },
                onSave = { title, y, m, d, hour, minute, priority, repeat, reminder ->
                    viewModel.saveTask(title, y, m, d, hour, minute, priority, repeat, reminder)
                }
            )
        }
    }
}
