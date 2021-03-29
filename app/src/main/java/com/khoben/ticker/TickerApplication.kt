package com.khoben.ticker

import android.app.Application
import com.khoben.ticker.common.CheckInternetConnection
import com.khoben.ticker.di.component.appComponent
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class TickerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        CheckInternetConnection.init(this)

        startKoin {
            androidLogger()
            androidContext(this@TickerApplication)
            modules(provide())
        }
    }

    private fun provide() = appComponent

    companion object {
        const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"
    }
}