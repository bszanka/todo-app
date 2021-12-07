package com.example.todo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todo.database.Task

@Dao
interface TaskDao {

    @Query("SELECT * from task_table")
    fun getAllTasks(): LiveData<List<Task>>

    @Insert
    fun insert(task: Task)

    @Query("DELETE FROM task_table")
    fun deleteAll()

    @Update
    fun update(task: Task)
 
    @Query("UPDATE task_table SET task = :task WHERE id == :id")
    fun updateItem(task: String, id: Int)

    @Delete
    fun deleteTask(task: Task)
}
