package com.khoben.ticker.di.component

import com.khoben.ticker.di.module.networkModule
import com.khoben.ticker.di.module.repositoryModule
import com.khoben.ticker.di.module.storageModule
import com.khoben.ticker.di.module.viewModelModule

val appComponent = listOf(networkModule, repositoryModule, viewModelModule, storageModule)