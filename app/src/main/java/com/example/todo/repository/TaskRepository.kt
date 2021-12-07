package com.example.todo.repository

import android.os.AsyncTask
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.todo.database.Task
import com.example.todo.database.TaskDao


class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    @WorkerThread
    fun insert(task: Task) {
        taskDao.insert(task)
    }

    fun deleteAll() {
        DeleteAllTasksAsyncTask(taskDao).execute()
    }

    private class DeleteAllTasksAsyncTask(private val mAsyncTaskDao: TaskDao) :
        AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            mAsyncTaskDao.deleteAll()
            return null
        }
    }

    fun deleteTask(task: Task) {
        DeleteTaskAsyncTask(taskDao).execute(task)
    }

    private class DeleteTaskAsyncTask(private val mAsyncTaskDao: TaskDao) :
        AsyncTask<Task, Void, Void>() {

        override fun doInBackground(vararg params: Task): Void? {
            mAsyncTaskDao.deleteTask(params[0])
            return null
        }
    }

    fun update(task: Task) {
        UpdateTaskAsyncTask(taskDao).execute(task)
    }

    private class UpdateTaskAsyncTask(private val mAsyncTaskDao: TaskDao) :
        AsyncTask<Task, Void, Void>() {
        override fun doInBackground(vararg params: Task?): Void? {
            mAsyncTaskDao.update(params[0]!!)
            return null
        }
    }
}
