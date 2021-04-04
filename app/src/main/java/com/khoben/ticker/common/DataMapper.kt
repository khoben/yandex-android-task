package com.khoben.ticker.common

import com.khoben.ticker.api.finnhub.http.model.CandleStockResult
import com.khoben.ticker.api.finnhub.http.model.CompanyNewsResult
import com.khoben.ticker.model.CandleStock
import com.khoben.ticker.model.News

fun CandleStockResult.toModel() = CandleStock(
    this.timestamps,
    this.closePrices
)

fun CompanyNewsResult.toModel() = News(
    this.datetime,
    this.headline,
    this.image,
    this.url
)