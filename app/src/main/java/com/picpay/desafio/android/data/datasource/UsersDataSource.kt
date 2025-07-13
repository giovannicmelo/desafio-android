package com.picpay.desafio.android.data.datasource

import com.picpay.desafio.android.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UsersDataSource {

    interface Local
    interface Remote {
        fun getUsers(): Flow<List<User>>
    }
}