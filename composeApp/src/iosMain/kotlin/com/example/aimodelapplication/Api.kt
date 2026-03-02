package com.example.aimodelapplication

import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.seanchinjunkai.firebase.ai.Firebase
import io.github.seanchinjunkai.firebase.ai.GenerativeBackend
import io.github.seanchinjunkai.firebase.ai.type.ImagePart
import io.github.seanchinjunkai.firebase.ai.type.ResponseModality
import io.github.seanchinjunkai.firebase.ai.type.generationConfig
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual suspend fun generateContent(prompt: String, modelName: String): GenerationResult {
    return try {
        val ai = Firebase.ai(backend = GenerativeBackend.googleAI())
        
        if (modelName.contains("imagen")) {
            GenerationResult.Error("Imagen model is not yet supported on iOS in this version.")
        } else if (modelName.contains("image")) {
            val model = ai.generativeModel(modelName, generationConfig = generationConfig {
                responseModalities = listOf(
                    ResponseModality.TEXT,
                    ResponseModality.IMAGE
                )
            })

            val imagePart = model.generateContent(prompt)
                .candidates.first().content.parts.filterIsInstance<ImagePart>()
                .firstOrNull()

            if (imagePart != null) {
                val uiImage = imagePart.image
                val nsData = UIImageJPEGRepresentation(uiImage, 1.0)
                if (nsData != null) {
                    val bytes = ByteArray(nsData.length.toInt())
                    bytes.usePinned { pinned ->
                        memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                    }
                    val skiaImage = Image.makeFromEncoded(bytes)
                    GenerationResult.Image(skiaImage.toComposeImageBitmap())
                } else {
                    GenerationResult.Error("Could not process generated image data")
                }
            } else {
                GenerationResult.Error("No image generated")
            }
        } else {
            val model = ai.generativeModel(modelName)
            val response = model.generateContent(prompt)
            GenerationResult.Text(response.text ?: "")
        }
    } catch (e: Exception) {
        GenerationResult.Error(e.message ?: "Unknown error")
    }
}
