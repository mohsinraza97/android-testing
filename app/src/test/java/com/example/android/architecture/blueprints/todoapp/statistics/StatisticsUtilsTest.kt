package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.junit.Assert.*

import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_empty_returnsZeros() {
        val result = getActiveAndCompletedStats(emptyList())

        assertEquals(result.activeTasksPercent, 0f)
        assertEquals(result.completedTasksPercent, 0f)
    }

    @Test
    fun getActiveAndCompletedStats_noActive_returnZeroHundred() {
        val tasks = listOf(
            Task("Task 1", "This is test task # 1", isCompleted = true)
        )

        val result = getActiveAndCompletedStats(tasks)

        assertEquals(result.activeTasksPercent, 0f)
        assertEquals(result.completedTasksPercent, 100f)
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        val result = getActiveAndCompletedStats(null)

        assertEquals(result.activeTasksPercent, 0f)
        assertEquals(result.completedTasksPercent, 0f)
    }

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        val tasks = listOf(
            Task("Task 1", "This is a test task # 1", isCompleted = false)
        )

        val result = getActiveAndCompletedStats(tasks)

        assertEquals(result.activeTasksPercent, 100f)
        assertEquals(result.completedTasksPercent, 0f)
    }

    @Test
    fun getActiveAndCompletedStats_partialCompleted_returnFortySixty() {
        val tasks = listOf(
            Task("Task 1", "This is test task # 1", isCompleted = true),
            Task("Task 2", "This is test task # 2", isCompleted = true),
            Task("Task 3", "This is test task # 3", isCompleted = false),
            Task("Task 4", "This is test task # 4", isCompleted = false),
            Task("Task 5", "This is test task # 5", isCompleted = false)
        )

        val result = getActiveAndCompletedStats(tasks)

        assertEquals(result.completedTasksPercent, 40f)
        assertEquals(result.activeTasksPercent, 60f)
    }
}