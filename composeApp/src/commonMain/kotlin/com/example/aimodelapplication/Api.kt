package com.example.aimodelapplication

expect suspend fun generateContent(prompt: String, modelName: String): String
