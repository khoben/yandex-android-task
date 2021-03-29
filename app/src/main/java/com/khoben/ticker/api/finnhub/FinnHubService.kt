package com.khoben.ticker.api.finnhub

import com.khoben.ticker.api.finnhub.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnHubService {
    @GET("search")
    suspend fun search(@Query("q") query: String): SearchResult

    @GET("stock/symbol")
    suspend fun allStockExchange(@Query("exchange") exchange: String = "US"): AllStockExchangeResult

    @GET("index/constituents?symbol=^GSPC")
    suspend fun sp500(): SP500Result

    @GET("stock/profile2")
    suspend fun companyProfile(@Query("symbol") symbol: String): CompanyProfileResult

    @GET("quote")
    suspend fun stockInfo(@Query("symbol") symbol: String): StockInfoResult

    @GET("company-news")
    suspend fun companyNews(
        @Query("symbol") symbol: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): List<CompanyNewsResult>
}