package com.example.aimodelapplication

data class StoredResponse(
    val id: String = "",
    val prompt: String = "",
    val model: String = "",
    val response: String = "",
    val type: String = "",
    val timestamp: Long = 0L
)
