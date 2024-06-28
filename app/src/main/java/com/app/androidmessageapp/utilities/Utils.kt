package com.app.androidmessageapp.utilities

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object Utils {
    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_NAME = "secret_key"

    fun saveSecretKey(context: Context, key: SecretKey) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val keyString = Base64.encodeToString(key.encoded, Base64.DEFAULT)
        editor.putString(KEY_NAME, keyString)
        editor.apply()
    }

    fun getSecretKey(context: Context): SecretKey? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val keyString = sharedPreferences.getString(KEY_NAME, null)
        return if (keyString != null) {
            val decodedKey = Base64.decode(keyString, Base64.DEFAULT)
            SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
        } else {
            null
        }
    }
}