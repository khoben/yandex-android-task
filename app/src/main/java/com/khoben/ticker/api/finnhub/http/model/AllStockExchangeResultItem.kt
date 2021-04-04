package com.khoben.ticker.api.finnhub.http.model

import kotlinx.serialization.Serializable

@Serializable
data class AllStockExchangeResultItem(
    val currency: String,
    val description: String,
    val displaySymbol: String,
    val figi: String,
    val mic: String,
    val symbol: String,
    val type: String
)