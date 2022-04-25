package com.mylibrary.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val enforceDarkTheme = false

        setContent {
            MaterialTheme(colors = if (isSystemInDarkTheme() || enforceDarkTheme) DarkColors else LightColors) {
                Editor()
            }
        }
    }
}