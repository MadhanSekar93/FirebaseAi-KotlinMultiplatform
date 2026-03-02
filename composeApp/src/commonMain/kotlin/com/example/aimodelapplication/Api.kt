package com.example.aimodelapplication

import androidx.compose.ui.graphics.ImageBitmap

sealed class GenerationResult {
    data class Text(val text: String) : GenerationResult()
    data class Image(val bitmap: ImageBitmap) : GenerationResult()
    data class Error(val message: String) : GenerationResult()
    object None : GenerationResult()
}

expect suspend fun generateContent(prompt: String, modelName: String): GenerationResult
