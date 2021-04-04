package com.khoben.ticker.api.finnhub.websocket.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinnHubSubscribeMessage(
    @SerialName("type")
    val type: String,
    @SerialName("symbol")
    val symbol: String
)