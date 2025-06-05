package com.example.puppy.service // Perubahan: Package name

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs_puppy", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("user_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("user_token", null)
    }

    fun clearToken() {
        prefs.edit().clear().apply()
    }
}