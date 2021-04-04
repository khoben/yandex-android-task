package com.khoben.ticker

import android.app.Application
import com.khoben.ticker.common.ConnectivityProvider
import com.khoben.ticker.di.component.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class TickerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        ConnectivityProvider.init(applicationContext)

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(provide())
        }
    }

    private fun provide() = appComponent
}