package com.picpay.desafio.android

import android.app.Application
import com.picpay.desafio.android.di.loadUserModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PicPayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@PicPayApplication)
            modules(loadUserModules())
        }
    }
}