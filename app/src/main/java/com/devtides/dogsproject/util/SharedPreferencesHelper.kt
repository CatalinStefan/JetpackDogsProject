package com.devtides.dogsproject.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper {

    companion object {

        private const val PREF_TIME = "Pref time"
        private var prefs : SharedPreferences? = null

        @Volatile private var instance: SharedPreferencesHelper? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): SharedPreferencesHelper = instance ?: synchronized(LOCK) {
            instance ?: buildHelper(context).also {
                instance = it
            }
        }

        private fun buildHelper(context: Context): SharedPreferencesHelper {
            prefs = context.getSharedPreferences("DogPrefs", 0)
            return SharedPreferencesHelper()
        }
    }

    fun saveUpdateTime(time: Long) {
        prefs?.edit()?.putLong(PREF_TIME, time)?.apply()
    }

    fun getUpdateTime() = prefs?.getLong(PREF_TIME, 0)
}