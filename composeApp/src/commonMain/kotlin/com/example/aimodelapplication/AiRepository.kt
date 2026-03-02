package com.example.aimodelapplication

expect class AiRepository() {
    fun fetchStoredResponses(callback: (List<StoredResponse>) -> Unit)
    fun saveResponseToDatabase(prompt: String, modelName: String, result: GenerationResult)
}
