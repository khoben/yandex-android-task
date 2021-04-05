package com.khoben.ticker.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khoben.ticker.model.Stock

class StockViewModel : ViewModel() {
    val stockData = MutableLiveData<Stock>()

    suspend fun getData() = stockData

    fun setStock(stock: Stock) {
        stockData.postValue(stock)
    }
}