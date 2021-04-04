package com.khoben.ticker.api.finnhub.websocket

import okhttp3.Interceptor
import okhttp3.Response

class FinnHubWSAuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url

        val url = originalUrl.newBuilder()
            .addQueryParameter("token", token)
            .build()

        val req = original.newBuilder().url(url).build()

        return chain.proceed(req)
    }
}