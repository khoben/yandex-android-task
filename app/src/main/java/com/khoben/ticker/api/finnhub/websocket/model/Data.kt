package com.khoben.ticker.api.finnhub.websocket.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("c")
    val tradeConditions: List<String>? = null,
    @SerialName("p")
    val lastPrice: Double? = null,
    @SerialName("s")
    val symbol: String? = null,
    @SerialName("t")
    val timestamp: Long? = null,
    @SerialName("v")
    val volume: Double? = null
)