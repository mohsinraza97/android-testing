/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 */
class DefaultTasksRepository(
    private val remoteDataSource: TasksDataSource,
    private val localDataSource: TasksDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : TasksRepository {

    companion object {
        @Volatile
        private var INSTANCE: DefaultTasksRepository? = null

        fun getRepository(app: Application): DefaultTasksRepository {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(app,
                    ToDoDatabase::class.java, "Tasks.db")
                    .build()
                DefaultTasksRepository(TasksRemoteDataSource, TasksLocalDataSource(database.taskDao())).also {
                    INSTANCE = it
                }
            }
        }
    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource()
            } catch (ex: Exception) {
                return Result.Error(ex)
            }
        }
        return localDataSource.getTasks()
    }

    override suspend fun refreshTasks() {
        updateTasksFromRemoteDataSource()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return localDataSource.observeTasks()
    }

    override suspend fun refreshTask(taskId: String) {
        updateTaskFromRemoteDataSource(taskId)
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        val remoteTasks = remoteDataSource.getTasks()

        if (remoteTasks is Success) {
            // Real apps might want to do a proper sync.
            localDataSource.deleteAllTasks()
            remoteTasks.data.forEach { task ->
                localDataSource.saveTask(task)
            }
        } else if (remoteTasks is Result.Error) {
            throw remoteTasks.exception
        }
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return localDataSource.observeTask(taskId)
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        val remoteTask = remoteDataSource.getTask(taskId)

        if (remoteTask is Success) {
            localDataSource.saveTask(remoteTask.data)
        }
    }

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (forceUpdate) {
            updateTaskFromRemoteDataSource(taskId)
        }
        return localDataSource.getTask(taskId)
    }

    override suspend fun saveTask(task: Task) {
        coroutineScope {
            launch { remoteDataSource.saveTask(task) }
            launch { localDataSource.saveTask(task) }
        }
    }

    override suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { remoteDataSource.completeTask(task) }
            launch { localDataSource.completeTask(task) }
        }
    }

    override suspend fun completeTask(taskId: String) {
        withContext(dispatcher) {
            (getTaskWithId(taskId) as? Success)?.let { it ->
                completeTask(it.data)
            }
        }
    }

    override suspend fun activateTask(task: Task) = withContext<Unit>(dispatcher) {
        coroutineScope {
            launch { remoteDataSource.activateTask(task) }
            launch { localDataSource.activateTask(task) }
        }
    }

    override suspend fun activateTask(taskId: String) {
        withContext(dispatcher) {
            (getTaskWithId(taskId) as? Success)?.let { it ->
                activateTask(it.data)
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { remoteDataSource.clearCompletedTasks() }
            launch { localDataSource.clearCompletedTasks() }
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(dispatcher) {
            coroutineScope {
                launch { remoteDataSource.deleteAllTasks() }
                launch { localDataSource.deleteAllTasks() }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { remoteDataSource.deleteTask(taskId) }
            launch { localDataSource.deleteTask(taskId) }
        }
    }

    private suspend fun getTaskWithId(id: String): Result<Task> {
        return localDataSource.getTask(id)
    }
}
