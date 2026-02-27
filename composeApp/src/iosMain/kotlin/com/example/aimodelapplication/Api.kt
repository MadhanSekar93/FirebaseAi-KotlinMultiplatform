package com.example.aimodelapplication

import io.github.seanchinjunkai.firebase.ai.Firebase
import io.github.seanchinjunkai.firebase.ai.GenerativeBackend

actual suspend fun generateContent(prompt: String, modelName: String): String {
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(modelName)
    val response = model.generateContent(prompt)
    return response.text ?: ""
}
