package com.example.aimodelapplication

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}
