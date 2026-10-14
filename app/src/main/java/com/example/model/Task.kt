package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val year: Int,
    val month: Int, // 1 - 12
    val day: Int,   // 1 - 31
    val hour: Int = 9,
    val minute: Int = 0,
    val priority: String = "মাঝারি", // "জরুরি", "উচ্চ", "মাঝারি", "কম"
    val repeat: String = "একবার",    // "একবার", "প্রতিদিন", "প্রতি সপ্তাহে", "প্রতি মাসে"
    val done: Boolean = false,
    val reminder: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
