package com.khoben.ticker.repository

import com.khoben.ticker.model.WebSocketTickerUpdate
import kotlinx.coroutines.flow.Flow

interface WebSocketRepository {
    fun startSocket(tickers: List<String>): Flow<WebSocketTickerUpdate>
    fun stopSocket()
}