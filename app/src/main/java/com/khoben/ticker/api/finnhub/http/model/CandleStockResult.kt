package com.khoben.ticker.api.finnhub.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CandleStockResult(
    @SerialName("c")
    val closePrices: List<Double>? = null,
    @SerialName("h")
    val highPrices: List<Double>? = null,
    @SerialName("l")
    val lowPrices: List<Double>? = null,
    @SerialName("o")
    val openPrices: List<Double>? = null,
    @SerialName("s")
    val requestStatus: String? = null, // ok or no_data
    @SerialName("t")
    val timestamps: List<Int>? = null,
    @SerialName("v")
    val volumes: List<Double>? = null
)