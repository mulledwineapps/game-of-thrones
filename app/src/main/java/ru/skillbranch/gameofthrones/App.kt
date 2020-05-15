package ru.skillbranch.gameofthrones

import android.app.Application
import android.content.Context

class App: Application() {
    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        // с контент-провайдерами не всё так тривиально: метод OnCreate вызывается не совсем самым первым
        // есть ситуации, когда вы можете обратиться к приложению и не получить из него контекст
        // это бывает крайне редко, но стоит об этом знать
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
    }
}