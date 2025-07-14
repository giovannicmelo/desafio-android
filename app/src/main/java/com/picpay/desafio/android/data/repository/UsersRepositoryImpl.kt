package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.data.datasource.UsersDataSource
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class UsersRepositoryImpl(
    private val remoteDataSource: UsersDataSource.Remote,
    private val localDataSource: UsersDataSource.Local
) : UsersRepository {

    override fun getUsers(): Flow<List<User>> {
        val cachedUsers = localDataSource.getCachedUsers()
        return if (cachedUsers.isNotEmpty()) {
            flow { emit(cachedUsers) }
        } else {
            remoteDataSource.getUsers()
                .onEach { users -> localDataSource.saveUsersInCache(users) }
                .catch { throw it }
        }
    }
}