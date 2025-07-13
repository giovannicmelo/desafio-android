package com.picpay.desafio.android.data.datasource

import com.picpay.desafio.android.data.api.PicPayService
import com.picpay.desafio.android.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UsersRemoteDataSourceImpl(private val api: PicPayService) : UsersDataSource.Remote {

    override fun getUsers(): Flow<List<User>> {

        return flow { emit(api.getUsers()) }
            .catch { throwable -> throw Throwable(throwable.message) }
            .map {
                it.map {
                    userResponse -> User(
                        userResponse.img,
                        userResponse.name,
                        userResponse.id,
                        userResponse.username
                    )
                }
            }
    }
}