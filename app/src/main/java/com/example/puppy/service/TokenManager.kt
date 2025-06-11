package com.example.puppy.service

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs_puppy", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val USER_ID = "user_id" // Kunci untuk menyimpan ID pengguna
    }

    // --- Fungsi untuk Token ---

    fun saveToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    // --- Fungsi untuk User ID (INI YANG DITAMBAHKAN) ---

    /**
     * Menyimpan ID pengguna ke SharedPreferences.
     * Panggil fungsi ini setelah user berhasil login.
     */
    fun saveUserId(userId: String) {
        prefs.edit().putString(USER_ID, userId).apply()
    }

    /**
     * Mengambil ID pengguna dari SharedPreferences.
     * Ini adalah fungsi yang dicari oleh ViewModel Anda.
     */
    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }


    /**
     * Menghapus semua data (token dan user ID).
     * Diperbarui untuk menghapus ID juga.
     */
    fun clearData() {
        prefs.edit().clear().apply()
    }
}