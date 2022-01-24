package com.example.android.architecture.blueprints.todoapp.data

import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.FakeDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")

    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    private lateinit var remoteDataSource: FakeDataSource
    private lateinit var localDataSource: FakeDataSource

    private lateinit var repository: DefaultTasksRepository

    @Before
    fun setup() {
        remoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        localDataSource = FakeDataSource(localTasks.toMutableList())

        repository = DefaultTasksRepository(remoteDataSource, localDataSource, Dispatchers.Unconfined)
    }

    @Test
    fun getTasks_requestAllTasksFromRemoteDataSource() = runBlockingTest {
        // When tasks are requested from the tasks repository
        val tasks = repository.getTasks(true) as Result.Success

        // Then tasks are loaded from the remote data source
        Assert.assertEquals(tasks.data, remoteTasks)
    }
}