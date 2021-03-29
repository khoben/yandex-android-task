package com.khoben.ticker.api.finnhub.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val count: Int,
    val result: List<StockFoundShortData>
)
