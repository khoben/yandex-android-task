package com.khoben.ticker.api.finnhub.model

import kotlinx.serialization.Serializable

@Serializable
data class StockFoundShortData(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String,
    var primary: List<String>? = null
)