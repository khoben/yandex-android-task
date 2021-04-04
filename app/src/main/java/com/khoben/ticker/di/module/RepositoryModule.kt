package com.khoben.ticker.di.module

import com.khoben.ticker.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    factory<RemoteStockRepository> { FinnhubRemoteRepository(get()) }
    factory<LocalStockRepository> { RoomLocalStockRepository(get(), get()) }
    factory<WebSocketRepository> { FinnHubWebSocketRepository(get()) }
}