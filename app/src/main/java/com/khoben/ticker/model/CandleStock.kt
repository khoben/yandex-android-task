package com.khoben.ticker.model

enum class CandleStockPeriod {
    DAY,
    WEEK,
    MONTH,
    SIXMONTH,
    ONEYEAR
}

data class CandleStock(
    val timestamps: List<Int>?,
    val prices: List<Double>?
)