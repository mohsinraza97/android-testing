package com.example.android.architecture.blueprints.todoapp

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario

fun <T : Activity> ActivityScenario<T>.getToolbarNavContentDescription(): String {
    var description = ""
    onActivity {
        description = it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }
    return description
}