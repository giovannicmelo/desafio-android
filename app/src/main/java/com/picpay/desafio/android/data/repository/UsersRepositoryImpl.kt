package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.data.datasource.UsersDataSource
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow

class UsersRepositoryImpl(
    private val remoteDataSource: UsersDataSource.Remote
) : UsersRepository {

    override fun getUsers(): Flow<List<User>> {
        return remoteDataSource.getUsers()
    }
}