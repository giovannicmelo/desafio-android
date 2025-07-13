package com.picpay.desafio.android.domain.usecase

import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(private val repository: UsersRepository) {

    operator fun invoke(): Flow<List<User>> {
        return repository.getUsers()
    }
}