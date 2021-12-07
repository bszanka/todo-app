package com.example.todo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var task: String)
