package com.khoben.ticker.repository

import com.khoben.ticker.model.*
import kotlinx.coroutines.flow.Flow

interface RemoteStockRepository {
    suspend fun getFirstSP500(amount: Int): Flow<DataState<Stock>>
    suspend fun getCompanyNewsLastWeek(ticker: String): Flow<DataState<News>>
    suspend fun candle(symbol: String, period: CandleStockPeriod): Flow<DataState<CandleStock>>
}