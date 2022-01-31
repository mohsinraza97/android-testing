package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTasksTestRepository
import com.example.android.architecture.blueprints.todoapp.util.TestDispatcherRule
import com.example.android.architecture.blueprints.todoapp.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
class TasksViewModelTest {

    private lateinit var repository: FakeTasksTestRepository
    private lateinit var viewModel: TasksViewModel

    // Executes each task synchronously using Architecture Components
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Utilized for replacing view model specific testing code so that it doesn't runs on main thread.
    // Execution on main thread via coroutines is not available in tests
    @get:Rule
    var testDispatcherRule = TestDispatcherRule()

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

    @Test
    fun completeTask_updatesDataAndShowSnackBar() {
        // Create an active task and add it to the repository.
        val task = Task("Active Task", "This is a test task!")
        repository.addTasks(task)

        // Mark the task as complete task.
        viewModel.completeTask(task, true)

        // Verify the task is completed.
        Assert.assertTrue(repository.currentTasks[task.id]?.isCompleted ?: false)

        // Assert that the snackbar has been updated with the correct text
        val snackBarText = viewModel.snackbarText.getOrAwaitValue()
        Assert.assertEquals(snackBarText.getContentIfNotHandled(), R.string.task_marked_complete)
    }
}