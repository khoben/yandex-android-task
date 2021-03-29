package com.khoben.ticker.api

object LogoPathProvider {
    fun get(ticker: String) = "https://storage.googleapis.com/iex/api/logos/${ticker}.png"
}