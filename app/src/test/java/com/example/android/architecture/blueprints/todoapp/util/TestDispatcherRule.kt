package com.example.android.architecture.blueprints.todoapp.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// JUnit rules are classes where you define generic testing code that executes before, after, or during a test.
// It's a way to take your code that would have been in @Before and @After,
// and put it in a class where it can be reused.

// This is a custom JUnit rule for utilizing TestCoroutineDispatcher
@ExperimentalCoroutinesApi
class TestDispatcherRule(
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher(),
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher) {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}