package com.example.aimodelapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform