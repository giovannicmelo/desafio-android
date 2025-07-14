package com.picpay.desafio.android.data.extension

import com.picpay.desafio.android.data.model.UserResponse
import com.picpay.desafio.android.domain.model.User

fun UserResponse.toDomain() = User(
    img = img ?: "",
    name = name ?: "",
    id = id ?: 0,
    username = username ?: ""
)

fun List<UserResponse>.toDomain() = map { it.toDomain() }

fun User.toResponse() = UserResponse(
    img = img,
    name = name,
    id = id,
    username = username
)

fun List<User>.toResponse() = map { it.toResponse() }