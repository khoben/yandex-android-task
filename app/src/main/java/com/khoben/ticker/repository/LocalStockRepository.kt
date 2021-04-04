package com.khoben.ticker.repository

import androidx.lifecycle.LiveData
import com.khoben.ticker.model.Stock

interface LocalStockRepository {
    fun allStocks(): LiveData<List<Stock>>?
    fun allFavoriteStocks(): LiveData<List<Stock>>?
    fun watchTicker(ticker: String): LiveData<Stock?>
    suspend fun search(query: String): List<Stock>?
    suspend fun getAllTickers(): List<String>?
    suspend fun countStocks(): Int
    suspend fun insert(stock: Stock)
    suspend fun getByTicker(ticker: String): Stock?
    suspend fun update(stock: Stock)
}