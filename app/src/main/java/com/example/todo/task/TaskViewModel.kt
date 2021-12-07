package com.example.todo.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.todo.database.Task
import com.example.todo.database.TaskRoomDatabase
import com.example.todo.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    init {
        val tasksDao = TaskRoomDatabase.getDatabase(application, scope).taskDao()
        repository = TaskRepository(tasksDao)
        allTasks = repository.allTasks
    }

    fun insert(task: Task) = scope.launch(Dispatchers.IO) {
        repository.insert(task)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun deleteAll() {
        repository.deleteAll()
    }

    fun deleteTask(task: Task) {
        repository.deleteTask(task)
    }

    fun updateTask(task: Task) {
        repository.update(task)
    }

}
