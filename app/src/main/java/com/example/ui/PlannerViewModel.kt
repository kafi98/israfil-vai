package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.TaskRepository
import com.example.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

data class SelectedDate(
    val year: Int,
    val month: Int, // 1 - 12
    val day: Int    // 1 - 31
)

class PlannerViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val monthlyGoal: StateFlow<String> = repository.getMonthlyGoal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val isDarkMode: StateFlow<Boolean?> = repository.getDarkMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val todayCalendar = Calendar.getInstance()
    private val _selectedDate = MutableStateFlow(
        SelectedDate(
            year = todayCalendar.get(Calendar.YEAR),
            month = todayCalendar.get(Calendar.MONTH) + 1,
            day = todayCalendar.get(Calendar.DAY_OF_MONTH)
        )
    )
    val selectedDate: StateFlow<SelectedDate> = _selectedDate.asStateFlow()

    // For calendar month navigation
    private val _calendarMonth = MutableStateFlow(
        Pair(
            todayCalendar.get(Calendar.YEAR),
            todayCalendar.get(Calendar.MONTH) + 1
        )
    )
    val calendarMonth: StateFlow<Pair<Int, Int>> = _calendarMonth.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _isTaskDialogOpen = MutableStateFlow(false)
    val isTaskDialogOpen: StateFlow<Boolean> = _isTaskDialogOpen.asStateFlow()

    private val _editingTask = MutableStateFlow<Task?>(null)
    val editingTask: StateFlow<Task?> = _editingTask.asStateFlow()

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setSelectedDate(year: Int, month: Int, day: Int) {
        _selectedDate.value = SelectedDate(year, month, day)
        _calendarMonth.value = Pair(year, month)
    }

    fun jumpToToday() {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        _selectedDate.value = SelectedDate(y, m, d)
        _calendarMonth.value = Pair(y, m)
    }

    fun shiftCalendarMonth(delta: Int) {
        val current = _calendarMonth.value
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, current.first)
        cal.set(Calendar.MONTH, current.second - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, delta)
        _calendarMonth.value = Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    fun openNewTaskDialog() {
        _editingTask.value = null
        _isTaskDialogOpen.value = true
    }

    fun openEditTaskDialog(task: Task) {
        _editingTask.value = task
        _isTaskDialogOpen.value = true
    }

    fun closeTaskDialog() {
        _isTaskDialogOpen.value = false
        _editingTask.value = null
    }

    fun saveTask(
        title: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        priority: String,
        repeat: String,
        reminder: Boolean
    ) {
        val existing = _editingTask.value
        viewModelScope.launch {
            if (existing != null) {
                repository.updateTask(
                    existing.copy(
                        title = title.trim(),
                        year = year,
                        month = month,
                        day = day,
                        hour = hour,
                        minute = minute,
                        priority = priority,
                        repeat = repeat,
                        reminder = reminder
                    )
                )
            } else {
                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    year = year,
                    month = month,
                    day = day,
                    hour = hour,
                    minute = minute,
                    priority = priority,
                    repeat = repeat,
                    done = false,
                    reminder = reminder
                )
                repository.insertTask(newTask)
            }
            closeTaskDialog()
        }
    }

    fun toggleTaskDone(task: Task, done: Boolean) {
        viewModelScope.launch {
            repository.updateTask(task.copy(done = done))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateMonthlyGoal(goal: String) {
        viewModelScope.launch {
            repository.setMonthlyGoal(goal)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }
}

class PlannerViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlannerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
