package com.khoben.ticker.api.finnhub.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class FinnHubAuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original  = chain.request()

        val req = original.newBuilder()
            .header("X-Finnhub-Token", "c1g30gv48v6r34ehpepg")
            .method(original.method(), original.body())
            .build()

        return chain.proceed(req)
    }
}