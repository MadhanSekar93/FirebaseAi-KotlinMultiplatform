package com.example.aimodelapplication

actual class AiRepository {
    actual fun fetchStoredResponses(callback: (List<StoredResponse>) -> Unit) {
        // TODO: Implement iOS Firestore fetching
        callback(emptyList())
    }

    actual fun saveResponseToDatabase(prompt: String, modelName: String, result: GenerationResult) {
        // TODO: Implement iOS Firestore saving
    }
}
