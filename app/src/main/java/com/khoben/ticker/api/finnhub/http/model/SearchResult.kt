package com.khoben.ticker.api.finnhub.http.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val count: Int,
    val result: List<StockFoundShortData>
)
