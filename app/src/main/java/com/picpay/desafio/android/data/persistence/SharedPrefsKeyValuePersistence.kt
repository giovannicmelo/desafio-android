package com.picpay.desafio.android.data.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

private const val PREFS_NAME = "shared_prefs"

class SharedPrefsKeyValuePersistence(
    private val context: Context
): KeyValuePersistence {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun saveData(data: String) {
        sharedPreferences.edit { putString(PREFS_NAME, data) }
    }

    override fun getData(): String? {
        return sharedPreferences.getString(PREFS_NAME, null)
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }
}
