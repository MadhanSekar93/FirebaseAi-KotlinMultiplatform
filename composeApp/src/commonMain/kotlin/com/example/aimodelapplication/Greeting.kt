package com.example.aimodelapplication

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }

    fun greetUser(): String{
        return "New Day, ${platform.name}!"
    }
}