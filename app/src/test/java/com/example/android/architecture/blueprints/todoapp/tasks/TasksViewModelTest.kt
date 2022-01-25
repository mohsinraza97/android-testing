package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTasksTestRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    private lateinit var repository: FakeTasksTestRepository
    private lateinit var viewModel: TasksViewModel

    // Executes each task synchronously using Architecture Components
    @get:Rule
    private var rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = FakeTasksTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        repository.addTasks(task1, task2, task3)

        viewModel = TasksViewModel(repository)
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {
        // 1. Given [Fresh TaskViewModel]
        // 2. When [Adding a new task]
        viewModel.addNewTask()

        // 3. Then [New task event is triggered]
        val value = viewModel.newTaskEvent.getOrAwaitValue()
        Assert.assertNotNull(value.getContentIfNotHandled())
    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {
        // 1. Given a fresh ViewModel
        // 2. When the filter type is ALL_TASKS
        viewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // 3. Then the "Add task" action is visible
        val visible = viewModel.tasksAddViewVisible.getOrAwaitValue()
        Assert.assertTrue(visible)
    }
}