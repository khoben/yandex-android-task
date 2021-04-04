package com.khoben.ticker.di.module

import com.khoben.ticker.ClientConfig
import com.khoben.ticker.TickerApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val endpointModule = module {
    single(named("finnhub_http")) {
        ClientConfig.FINNHUB_BASE_URL
    }
    single(named("finnhub_ws")) {
        ClientConfig.FINNHUB_WS_ENDPOINT
    }
    single(named("finnhub_token")) {
        ClientConfig.FINNHUB_API_TOKEN
    }
}