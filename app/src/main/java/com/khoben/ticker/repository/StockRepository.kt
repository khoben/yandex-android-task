package com.khoben.ticker.repository

import com.khoben.ticker.model.Stock

interface StockRepository {
    suspend fun searchStocks(): List<Stock>
}