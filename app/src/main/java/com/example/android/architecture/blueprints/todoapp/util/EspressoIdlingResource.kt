package com.example.android.architecture.blueprints.todoapp.util

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"

    /**
     * CountingIdlingResource allows you to increment and decrement a counter such that:
     * When the counter is greater than zero, the app is considered working.
     * When the counter is zero, the app is considered idle.
     */
    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(call: () -> T): T {
    // Espresso does not work well with coroutines yet. See
    // https://github.com/Kotlin/kotlinx.coroutines/issues/982
    EspressoIdlingResource.increment() // Set app as busy
    return try {
        call()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle
    }
}