package com.example.android.architecture.blueprints.todoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {

    private lateinit var repository: TasksRepository

    // An idling resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        /**
         * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
         * are not scheduled in the main Looper (for example when executed on a different thread)
         */
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)

        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()

        /**
         * Unregister your Idling Resource so it can be garbage collected and does not leak any memory
         */
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    // runBlocking is used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
    // TODO: Replace with runBlockingTest once issue is resolved
    @Test
    fun editTask() = runBlocking {
        // Set initial state
        val title = "Task # 1"
        val desc = "Description # 1 - Active Task"
        repository.saveTask(Task(title, desc))

        // Start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Espresso code will go here
        // 1. Click on the task on the list and verify that all the data is correct
        onView(withText(title)).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText(title)))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText(desc)))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isNotChecked()))
        // 2. Click on the edit button, edit, and save
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())
        // 3. Verify task is displayed on screen in the task list
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // 4. Verify previous task is not displayed
        onView(withText(title)).check(doesNotExist())

        // Make sure the activity is closed before resetting the db
        activityScenario.close()
    }

    @Test
    fun createOneTask_deleteTask() = runBlocking {
        // 1. Start TasksActivity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 2. Add an active task by clicking on the FAB and saving a new task
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(typeText("New Task"),
            closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("Adding a dummy task"),
            closeSoftKeyboard())
        onView(withId(R.id.save_task_fab)).perform(click())

        // 3. Open the new task in a details view
        onView(withText("New Task")).perform(click())

        // 4. Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // 5. Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("New Task")).check(doesNotExist())

        // 6. Make sure the activity is closed
        activityScenario.close()
    }
}