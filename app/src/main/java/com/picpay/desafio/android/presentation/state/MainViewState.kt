package com.picpay.desafio.android.presentation.state

import com.picpay.desafio.android.domain.model.User

data class MainViewState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList()
)