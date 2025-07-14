package com.picpay.desafio.android.data.datasource

import com.picpay.desafio.android.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UsersDataSource {

    interface Local {
        fun getCachedUsers(): List<User>
        fun saveUsersInCache(users: List<User>)
    }
    interface Remote {
        fun getUsers(): Flow<List<User>>
    }
}