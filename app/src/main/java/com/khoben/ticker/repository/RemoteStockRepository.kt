package com.khoben.ticker.repository

import com.khoben.ticker.model.CandleStock
import com.khoben.ticker.model.CandleStockPeriod
import com.khoben.ticker.model.DataState
import com.khoben.ticker.model.News
import kotlinx.coroutines.flow.Flow

interface RemoteStockRepository {
    suspend fun getFirstSP500(amount: Int): Flow<DataState<*>>?
    suspend fun getCompanyNewsLastWeek(ticker: String): List<News>?
    suspend fun candle(symbol: String, period: CandleStockPeriod): CandleStock?
}