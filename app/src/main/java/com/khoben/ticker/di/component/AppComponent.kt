package com.khoben.ticker.di.component

import com.khoben.ticker.di.module.*

val appComponent =
    listOf(networkModule, repositoryModule, viewModelModule, storageModule, endpointModule)