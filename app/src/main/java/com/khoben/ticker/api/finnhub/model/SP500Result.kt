package com.khoben.ticker.api.finnhub.model

import kotlinx.serialization.Serializable

@Serializable
data class SP500Result(
    val constituents: List<String>,
    val symbol: String
)