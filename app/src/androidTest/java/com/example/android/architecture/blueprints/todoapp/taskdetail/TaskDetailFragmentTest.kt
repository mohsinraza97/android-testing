package com.example.android.architecture.blueprints.todoapp.taskdetail

import FakeTasksAndroidTestRepository
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun setup() {
        repository = FakeTasksAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @Test
    fun activeTaskDetails_DisplayedInUi() = runBlockingTest {
        // GIVEN - Add active (incomplete) task to the DB
        val activeTask = Task("Active Task # 1", "This is a test active task.")
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

        Thread.sleep(5000)
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }
}