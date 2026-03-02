package com.example.aimodelapplication

import android.provider.Settings
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

actual class AiRepository {
    private val db = Firebase.firestore
    private val deviceId: String by lazy {
        val context = MyApplication.instance
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
    }

    actual fun fetchStoredResponses(callback: (List<StoredResponse>) -> Unit) {
        db.collection("ai_responses").document(deviceId).collection("responses")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    Log.w("AiRepository", "Listen failed.", exception)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val responses = snapshots.map { snapshot ->
                        snapshot.toObject(StoredResponse::class.java).copy(id = snapshot.id)
                    }.reversed()
                    callback(responses)
                }
            }
    }

    actual fun saveResponseToDatabase(prompt: String, modelName: String, result: GenerationResult) {
        val data = mutableMapOf<String, Any>(
            "prompt" to prompt,
            "model" to modelName,
            "timestamp" to System.currentTimeMillis()
        )

        when (result) {
            is GenerationResult.Text -> {
                data["response"] = result.text
                data["type"] = "text"
            }
            is GenerationResult.Image -> {
                data["response"] = "Image generated"
                data["type"] = "image"
            }
            is GenerationResult.Error -> {
                data["response"] = result.message
                data["type"] = "error"
            }
            else -> return
        }

        db.collection("ai_responses").document(deviceId).collection("responses").add(data)
            .addOnSuccessListener {
                Log.d("AiRepository", "Successfully stored response")
            }
            .addOnFailureListener { e ->
                Log.e("AiRepository", "Failed to store response", e)
            }
    }
}
