package com.picpay.desafio.android.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import com.picpay.desafio.android.presentation.state.MainViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class MainViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _state = MutableLiveData(MainViewState())
    val state: LiveData<MainViewState> = _state

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        getUsers()
    }

    private fun getUsers() = getUsersUseCase.invoke()
        .flowOn(dispatcher)
        .onStart {
            _state.value = _state.value?.copy(isLoading = true)
        }
        .onCompletion {
            _state.value = _state.value?.copy(isLoading = false)
        }
        .catch {
            _error.value = it.message
        }
        .onEach {
            _state.value = _state.value?.copy(users = it)
        }
        .launchIn(viewModelScope)
}