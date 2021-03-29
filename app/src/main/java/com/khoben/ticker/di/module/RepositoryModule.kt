package com.khoben.ticker.di.module

import com.khoben.ticker.repository.FinnhubRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { FinnhubRepository(get()) }
}