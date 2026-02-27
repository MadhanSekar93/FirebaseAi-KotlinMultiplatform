package com.example.aimodelapplication

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

actual suspend fun generateContent(prompt: String, modelName: String): String {
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(modelName)
    val response = model.generateContent(prompt)
    return response.text ?: ""
}
