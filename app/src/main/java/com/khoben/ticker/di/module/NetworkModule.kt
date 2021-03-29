package com.khoben.ticker.di.module

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.khoben.ticker.TickerApplication
import com.khoben.ticker.api.finnhub.FinnHubService
import com.khoben.ticker.api.finnhub.interceptor.FinnHubAuthInterceptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import timber.log.Timber

@ExperimentalSerializationApi
val networkModule = module {

    factory<Interceptor>(named("logger")) {
        HttpLoggingInterceptor {
            Timber.d(it.toString())
        }.setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    factory<Interceptor>(named("finnhub_auth")) {
        FinnHubAuthInterceptor()
    }

    factory {
        OkHttpClient.Builder()
            .addInterceptor(get(named("finnhub_auth")))
            .addInterceptor(get(named("logger")))
            .build()
    }

    single<Retrofit>(named("finnhub_api")) {
        Retrofit.Builder()
            .client(get())
            .baseUrl(TickerApplication.FINNHUB_BASE_URL)
            .addConverterFactory(Json.asConverterFactory(MediaType.parse("application/json")!!))
            .build()
    }

    factory<FinnHubService> {
        get<Retrofit>(named("finnhub_api")).create(FinnHubService::class.java)
    }
}