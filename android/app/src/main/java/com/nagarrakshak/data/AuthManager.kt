package com.nagarrakshak.data

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("nagarrakshak_auth_prefs", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)
        set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

    var isGuest: Boolean
        get() = prefs.getBoolean("is_guest", false)
        set(value) = prefs.edit().putBoolean("is_guest", value).apply()

    var userName: String?
        get() = prefs.getString("user_name", null)
        set(value) = prefs.edit().putString("user_name", value).apply()

    var userEmail: String?
        get() = prefs.getString("user_email", null)
        set(value) = prefs.edit().putString("user_email", value).apply()

    var userPhotoUrl: String?
        get() = prefs.getString("user_photo_url", null)
        set(value) = prefs.edit().putString("user_photo_url", value).apply()

    var loginType: String?
        get() = prefs.getString("login_type", null) // "google", "email", "guest"
        set(value) = prefs.edit().putString("login_type", value).apply()

    fun loginWithGoogle(email: String, name: String, photoUrl: String?) {
        isLoggedIn = true
        isGuest = false
        userEmail = email
        userName = name
        userPhotoUrl = photoUrl
        loginType = "google"
    }

    fun loginWithEmail(email: String, name: String) {
        isLoggedIn = true
        isGuest = false
        userEmail = email
        userName = name
        userPhotoUrl = null
        loginType = "email"
    }

    fun loginAsGuest() {
        isLoggedIn = false
        isGuest = true
        userEmail = "guest@nagarrakshak.org"
        userName = "Guest Citizen"
        userPhotoUrl = null
        loginType = "guest"
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
