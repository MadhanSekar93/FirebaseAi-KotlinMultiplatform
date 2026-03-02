package com.example.aimodelapplication

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ImagenAspectRatio
import com.google.firebase.ai.type.ImagenGenerationConfig
import com.google.firebase.ai.type.ImagenImageFormat
import com.google.firebase.ai.type.ImagenPersonFilterLevel
import com.google.firebase.ai.type.ImagenSafetyFilterLevel
import com.google.firebase.ai.type.ImagenSafetySettings
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.generationConfig

@OptIn(PublicPreviewAPI::class)
actual suspend fun generateContent(prompt: String, modelName: String): GenerationResult {
    return try {
        val ai = Firebase.ai(backend = GenerativeBackend.googleAI())
        val config = ImagenGenerationConfig(
            numberOfImages = 1,
            aspectRatio = ImagenAspectRatio.LANDSCAPE_16x9,
            imageFormat = ImagenImageFormat.jpeg(compressionQuality = 100)
        )
        if(modelName.contains("imagen"))
        {
            val model = ai.imagenModel(modelName,config,safetySettings = ImagenSafetySettings(
                 safetyFilterLevel = ImagenSafetyFilterLevel.BLOCK_LOW_AND_ABOVE,
                 personFilterLevel = ImagenPersonFilterLevel.ALLOW_ALL
             )
             )
             val response = model.generateImages(prompt)
             val image = response.images.firstOrNull()
             if (image != null) {
                 val bitmap = BitmapFactory.decodeByteArray(image.data, 0, image.data.size)
                 GenerationResult.Image(bitmap.asImageBitmap())
             } else {
                 GenerationResult.Error("No image generated")
             }
        }else
        if (modelName.contains("image")) {

            val model = ai.generativeModel(modelName,generationConfig = generationConfig {
                responseModalities = listOf(
                    ResponseModality.TEXT,
                    ResponseModality.IMAGE
                )
            })

            val generatedImageAsBitmap: Bitmap? = model.generateContent(prompt)
                .candidates.first().content.parts.filterIsInstance<ImagePart>()
                .firstOrNull()?.image
            GenerationResult.Image(generatedImageAsBitmap!!.asImageBitmap())

        } else {
            val model = ai.generativeModel(modelName)
            val response = model.generateContent(prompt)
            GenerationResult.Text(response.text ?: "")
        }
    } catch (e: Exception) {
        GenerationResult.Error(e.message ?: "Unknown error")
    }
}
