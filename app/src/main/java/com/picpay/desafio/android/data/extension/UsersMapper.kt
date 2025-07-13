package com.picpay.desafio.android.data.extension

import com.picpay.desafio.android.data.model.UserResponse
import com.picpay.desafio.android.domain.model.User

fun UserResponse.toDomain() = User(
    img = img ?: "",
    name = name ?: "",
    id = id ?: 0,
    username = username ?: ""
)