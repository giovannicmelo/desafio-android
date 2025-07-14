package com.picpay.desafio.android.data.persistence

interface KeyValuePersistence {

    fun saveData(data: String)
    fun getData(): String?
    fun clear()
}