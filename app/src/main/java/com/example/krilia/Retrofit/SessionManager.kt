package com.example.krilia.Retrofit

import android.content.Context
import android.content.SharedPreferences
import com.example.krilia.R

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun tokenExist(): Boolean{
        return prefs.contains(USER_TOKEN)
    }

    fun deleteToken(){
        prefs.edit().remove(USER_TOKEN).apply()
    }


    fun saveUserId(id: Int) {
        val editor = prefs.edit()
        editor.putString(USER_ID, id.toString())
        editor.apply()
    }
    fun fetchUserId(): String? {
        return prefs.getString(USER_ID, null)
    }
}