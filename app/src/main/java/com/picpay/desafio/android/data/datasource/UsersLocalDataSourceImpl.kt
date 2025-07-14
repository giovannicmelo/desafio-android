package com.picpay.desafio.android.data.datasource

import com.google.gson.Gson
import com.picpay.desafio.android.data.extension.toDomain
import com.picpay.desafio.android.data.extension.toResponse
import com.picpay.desafio.android.data.model.UserResponse
import com.picpay.desafio.android.data.persistence.KeyValuePersistence
import com.picpay.desafio.android.domain.model.User

class UsersLocalDataSourceImpl(private val persistence: KeyValuePersistence): UsersDataSource.Local {

    override fun getCachedUsers(): List<User> {
        val cache = persistence.getData()
        return runCatching {
            Gson().fromJson(cache, Array<UserResponse>::class.java).toList().toDomain()
        }.getOrDefault(emptyList())
    }

    override fun saveUsersInCache(users: List<User>) {
        runCatching {
            val cache = Gson().toJson(users.toResponse())
            persistence.saveData(cache)
        }.onFailure {
            persistence.clear()
        }
    }
}