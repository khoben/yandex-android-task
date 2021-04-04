package com.khoben.ticker.api.finnhub.websocket.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinnHubWebSocketResponse(
    @SerialName("data")
    val data: List<Data>? = null,
    @SerialName("type")
    val type: String? = null
)