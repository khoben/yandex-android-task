package com.khoben.ticker.repository

import androidx.lifecycle.LiveData
import com.khoben.ticker.common.StockImageRemoteDownloader
import com.khoben.ticker.database.dao.StockDao
import com.khoben.ticker.model.Stock

class RoomLocalStockRepository(
    private val stockDao: StockDao,
    private val stockImageRemoteDownloader: StockImageRemoteDownloader
) : LocalStockRepository {
    override fun allStocks(): LiveData<List<Stock>>? = stockDao.all
    override fun allFavoriteStocks(): LiveData<List<Stock>>? = stockDao.favorite
    override fun watchTicker(ticker: String): LiveData<Stock?> = stockDao.watchTicker(ticker)
    override suspend fun search(query: String): List<Stock>? {
        if (query.isBlank()) return emptyList()
        return stockDao.search(query)
    }

    override suspend fun getAllTickers() = stockDao.getAllTickerSymbols()
    override suspend fun countStocks(): Int = stockDao.count()
    override suspend fun insert(stock: Stock) {
        val insertedId = stockDao.insert(stock)
        stockImageRemoteDownloader.downloadLogo(stock)?.let { localImage ->
            stockDao.update(stock.apply {
                id = insertedId
                logo = localImage.absolutePath
            })
        }
    }

    override suspend fun getByTicker(ticker: String) = stockDao.getByTicker(ticker)
    override suspend fun update(stock: Stock) = stockDao.update(stock)
}