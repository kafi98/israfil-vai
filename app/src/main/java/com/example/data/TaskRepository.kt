package com.example.data

import com.example.model.AppSetting
import com.example.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksForDate(year: Int, month: Int, day: Int): Flow<List<Task>> =
        taskDao.getTasksForDate(year, month, day)

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteById(id: String) = taskDao.deleteById(id)

    fun getMonthlyGoal(): Flow<String> =
        taskDao.getSetting("monthly_goal").map { it ?: "" }

    suspend fun setMonthlyGoal(goal: String) {
        taskDao.insertSetting(AppSetting("monthly_goal", goal))
    }

    fun getDarkMode(): Flow<Boolean?> =
        taskDao.getSetting("dark_mode").map { it?.toBooleanStrictOrNull() }

    suspend fun setDarkMode(enabled: Boolean) {
        taskDao.insertSetting(AppSetting("dark_mode", enabled.toString()))
    }
}
