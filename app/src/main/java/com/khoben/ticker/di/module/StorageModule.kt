package com.khoben.ticker.di.module

import com.khoben.ticker.common.StockImageRemoteDownloader
import com.khoben.ticker.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    single { AppDatabase.getInstance(androidContext()).stockDao() }
    factory { StockImageRemoteDownloader(androidContext()) }
}