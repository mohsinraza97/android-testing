package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    // Executes each task synchronously using Architecture Components
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun setup() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        // GIVEN - Insert a task
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // WHEN - Get the task by id from the database
        val loadedTask = database.taskDao().getTaskById(task.id)

        // THEN - The loaded data contains the expected values
        Assert.assertNotNull(loadedTask)
        Assert.assertEquals(loadedTask?.id, task.id)
        Assert.assertEquals(loadedTask?.title, task.title)
        Assert.assertEquals(loadedTask?.description, task.description)
        Assert.assertEquals(loadedTask?.isCompleted, task.isCompleted)
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest{
        // 1. GIVEN - Insert a task into the DAO
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // 2. GIVEN - Update the task by creating a new task with the same ID but different attributes
        val newTask = task.copy("<Task>", "<Description>", true)
        database.taskDao().updateTask(newTask)

        // 3. WHEN - You get the task by its ID,
        val loadedTask = database.taskDao().getTaskById(newTask.id)

        // 4. THEN - Check it has updated values
        Assert.assertEquals(loadedTask?.title, "<Task>")
        Assert.assertEquals(loadedTask?.description, "<Description>")
        Assert.assertEquals(loadedTask?.isCompleted, true)
        Assert.assertEquals(loadedTask?.id, task.id)
    }
}