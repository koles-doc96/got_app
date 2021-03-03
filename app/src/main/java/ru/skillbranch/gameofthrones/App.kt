package ru.skillbranch.gameofthrones

import android.app.Application
import android.content.Context


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object{
        private lateinit var context : Context

        fun applicationContext() : Context{
            return context
        }
    }
}