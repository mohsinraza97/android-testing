package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTasksTestRepository
import com.example.android.architecture.blueprints.todoapp.util.TestDispatcherRule
import com.example.android.architecture.blueprints.todoapp.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    private lateinit var repository: FakeTasksTestRepository
    private lateinit var viewModel: StatisticsViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testDispatcherRule = TestDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeTasksTestRepository()
        viewModel = StatisticsViewModel(repository)
    }

    @Test
    fun loadTasks_loading() {
        // Pause dispatcher so you can verify initial values.
        testDispatcherRule.pauseDispatcher()

        // Load the task in the view model.
        viewModel.refresh()

        // Then assert that the progress indicator is shown.
        Assert.assertEquals(viewModel.dataLoading.getOrAwaitValue(), true)

        // Execute pending coroutines actions.
        testDispatcherRule.resumeDispatcher()

        // Then assert that the progress indicator is hidden.
        Assert.assertEquals(viewModel.dataLoading.getOrAwaitValue(), false)
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors.
        repository.setReturnError(true)
        viewModel.refresh()

        Assert.assertTrue(viewModel.empty.getOrAwaitValue())
        Assert.assertTrue(viewModel.error.getOrAwaitValue())
    }
}