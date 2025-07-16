package com.picpay.desafio.android.presentation.action

sealed class MainViewAction {

    data class ShowErrorMessage(val message: String) : MainViewAction()
}