package com.example.aimodelapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    var generationResult by mutableStateOf<GenerationResult>(GenerationResult.None)
        private set

    val storedResponses = mutableStateListOf<StoredResponse>()

    private val repository = AiRepository()

    init {
        fetchStoredResponses()
    }

    private fun fetchStoredResponses() {
        repository.fetchStoredResponses { responses ->
            storedResponses.clear()
            storedResponses.addAll(responses)
        }
    }

    fun generateContent(prompt: String, modelName: String) {
        generationResult = GenerationResult.None
        viewModelScope.launch {
            try {
                val result = com.example.aimodelapplication.generateContent(prompt, modelName)
                generationResult = result
                repository.saveResponseToDatabase(prompt, modelName, result)
            } catch (e: Exception) {
                generationResult = GenerationResult.Error(e.message ?: "Could not generate response.")
            }
        }
    }
}
