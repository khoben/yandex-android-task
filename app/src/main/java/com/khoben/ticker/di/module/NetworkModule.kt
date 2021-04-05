package com.khoben.ticker.di.module

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.khoben.ticker.BuildConfig
import com.khoben.ticker.ClientConfig
import com.khoben.ticker.api.finnhub.http.FinnHubService
import com.khoben.ticker.api.finnhub.http.interceptor.FinnHubAuthInterceptor
import com.khoben.ticker.api.finnhub.websocket.FinnHubWSAuthInterceptor
import com.khoben.ticker.api.finnhub.websocket.FinnHubWSService
import com.khoben.ticker.api.finnhub.websocket.FinnHubWebSocket
import com.khoben.ticker.api.finnhub.websocket.asMessageAdapterFactory
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.retry.LinearBackoffStrategy
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import get
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

@ExperimentalSerializationApi
val networkModule = module {

    factory(named("logger")) {
        HttpLoggingInterceptor { Timber.d(it) }.setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    factory(named("finnhub_auth")) {
        FinnHubAuthInterceptor(get(named("finnhub_token")))
    }

    factory(named("finnhub_ws_auth")) {
        FinnHubWSAuthInterceptor(get(named("finnhub_token")))
    }

    factory(named("finnhub")) {
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG)
                    addInterceptor(get(named("logger")) as HttpLoggingInterceptor)
            }
            .addInterceptor(get(named("finnhub_auth")) as FinnHubAuthInterceptor)
            .build()
    }

    factory(named("finnhub_ws")) {
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG)
                    addInterceptor(get(named("logger")) as HttpLoggingInterceptor)
            }
            .addInterceptor(get(named("finnhub_ws_auth")) as FinnHubWSAuthInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(40, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .followRedirects(false)
            .build()
    }

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single<Retrofit>(named("finnhub_api")) {
        Retrofit.Builder()
            .client(get(named("finnhub")))
            .baseUrl(get(named("finnhub_http")) as String)
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaTypeOrNull()!!))
            .build()
    }

    factory<FinnHubService> {
        get<Retrofit>(named("finnhub_api")).create(FinnHubService::class.java)
    }

    single<FinnHubWSService> {
        Scarlet(
            OkHttpWebSocket(
                get(named("finnhub_ws")),
                OkHttpWebSocket.SimpleRequestFactory(
                    { Request.Builder().url(get(named("finnhub_ws")) as String).build() },
                    { ShutdownReason.GRACEFUL }
                )
            ),
            Scarlet.Configuration(
                backoffStrategy = LinearBackoffStrategy(ClientConfig.WS_RECONNECT_INTERVAL),
                messageAdapterFactories = listOf(Json.asMessageAdapterFactory()),
                streamAdapterFactories = listOf(CoroutinesStreamAdapterFactory()),
                lifecycle = AndroidLifecycle.ofApplicationForeground(androidApplication()),
                debug = true
            )
        ).create()
    }

    single {
        FinnHubWebSocket(get())
    }
}