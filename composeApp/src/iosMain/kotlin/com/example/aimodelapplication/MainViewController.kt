package com.example.aimodelapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.launch

fun MainViewController() = ComposeUIViewController {
    var responseText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    App(
        onPromptChange = { prompt, modelName ->
            scope.launch {
                try {
                    responseText = generateContent(prompt, modelName)
                } catch (e: Exception) {
                    responseText = "Error: Could not generate response. ${e.message}"
                }
            }
        },
        onGenerateClick = {},
        responseText = responseText
    )
}
