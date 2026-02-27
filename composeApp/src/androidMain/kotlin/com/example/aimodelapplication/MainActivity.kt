package com.example.aimodelapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var responseText by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                onPromptChange = { prompt, modelName ->
                    lifecycleScope.launch {
                        try {
                            responseText = generateContent(prompt, modelName)
                        } catch (e: Exception) {
                            Log.e("GeminiAi", "Error generating content", e)
                            responseText = "Error: Could not generate response."
                        }
                    }
                },
                onGenerateClick = {},
                responseText = responseText
            )
        }
    }
}
