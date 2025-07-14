package com.picpay.desafio.android.di

import com.picpay.desafio.android.data.api.PicPayService
import com.picpay.desafio.android.data.datasource.UsersLocalDataSourceImpl
import com.picpay.desafio.android.data.datasource.UsersRemoteDataSourceImpl
import com.picpay.desafio.android.data.persistence.SharedPrefsKeyValuePersistence
import com.picpay.desafio.android.data.repository.UsersRepositoryImpl
import com.picpay.desafio.android.data.service.ServiceClient
import com.picpay.desafio.android.domain.repository.UsersRepository
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import com.picpay.desafio.android.presentation.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

private val userModules: Module = module {
    factory<UsersRepository> {
        UsersRepositoryImpl(
            remoteDataSource = UsersRemoteDataSourceImpl(
                api = ServiceClient.create(PicPayService::class.java)
            ),
            localDataSource = UsersLocalDataSourceImpl(
                persistence = SharedPrefsKeyValuePersistence(
                    context = androidApplication()
                )
            )
        )
    }

    factory { GetUsersUseCase(repository = get()) }

    viewModel { MainViewModel(getUsersUseCase = get()) }
}

fun loadUserModules(): List<Module> {
    return listOf(userModules)
}