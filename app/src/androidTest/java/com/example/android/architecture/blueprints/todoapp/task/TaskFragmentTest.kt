package com.example.android.architecture.blueprints.todoapp.task

import FakeTasksAndroidTestRepository
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragment
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragmentDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class TaskFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun setup() {
        repository = FakeTasksAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @Test
    fun clickTask_navigateToDetailFragment() = runBlockingTest {
        repository.apply {
            saveTask(Task("Title 1", "Description 1", false, "T101"))
            saveTask(Task("Title 2", "Description 2", false, "T102"))
            saveTask(Task("Title 3", "Description 3", true, "T103"))
            saveTask(Task("Title 4", "Description 4", false, "T104"))
        }

        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { frag ->
            frag.view?.let { it -> Navigation.setViewNavController(it, navController) }
        }

        // WHEN - Click on the third list item
        onView(withId(R.id.tasks_list)).perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
            hasDescendant(withText("Title 3")), click()
        ))

        // THEN - Verify that we navigate to the third detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("T103")
        )
    }

    @Test
    fun clickAddTaskFAB_navigateToAddEditFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { frag ->
            frag.view?.let { it -> Navigation.setViewNavController(it, navController) }
        }

        // WHEN - Click on the "+" button
        onView(withId(R.id.add_task_fab)).perform(click())

        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
            null, getApplicationContext<Context>().getString(R.string.add_task)
        ))
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }
}