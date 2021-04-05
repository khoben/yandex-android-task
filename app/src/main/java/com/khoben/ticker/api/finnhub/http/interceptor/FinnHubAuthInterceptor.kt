package com.khoben.ticker.api.finnhub.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class FinnHubAuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val req = original.newBuilder()
            .header("X-Finnhub-Token", token)
            .build()

        return chain.proceed(req)
    }
}