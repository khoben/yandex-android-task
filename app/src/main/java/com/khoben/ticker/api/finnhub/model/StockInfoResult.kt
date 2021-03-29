package com.khoben.ticker.api.finnhub.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockInfoResult(
    @SerialName("c") val currentPrice: Double,
    @SerialName("h") val highDayPrice: Double,
    @SerialName("l") val lowDayPrice: Double,
    @SerialName("o") val openDayPrice: Double,
    @SerialName("pc") val previousClosePrice: Double,
    @SerialName("t") val timestamp: Long
)
