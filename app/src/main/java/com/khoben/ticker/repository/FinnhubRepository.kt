package com.khoben.ticker.repository

import com.khoben.ticker.api.finnhub.FinnHubService
import com.khoben.ticker.api.finnhub.model.StockInfoResult
import com.khoben.ticker.model.Stock

class FinnhubRepository(private val service: FinnHubService) : StockRepository {

    private inline fun <T> safeApiCall(responseFunction: () -> T): T? {
        return try {
            responseFunction.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun search(query: String) = safeApiCall { service.search(query) }
    private suspend fun sp500() = safeApiCall { service.sp500() }
    private suspend fun allStockExchange() = safeApiCall { service.allStockExchange() }
    private suspend fun companyProfile(symbol: String) =
        safeApiCall { service.companyProfile(symbol) }

    private suspend fun stockInfo(symbol: String) = safeApiCall { service.stockInfo(symbol) }
    private suspend fun companyNews(symbol: String, from: String, to: String) =
        safeApiCall { service.companyNews(symbol, from, to) }

    private fun calcPriceChangeDailyPercentage(priceChange: Double, currentPrice: Double): Double {
        return priceChange / currentPrice * 100
    }

    private fun calcPriceChangeDailyPrice(stockInfo: StockInfoResult): Double {
        return stockInfo.currentPrice - stockInfo.previousClosePrice
    }

    override suspend fun searchStocks(): List<Stock> {
        val stocks = mutableListOf<Stock>()
        val allStocks = sp500() ?: return emptyList()
        // getting some stocks
        for (ticker in allStocks.constituents.subList(0, 30)) {
            val stockQuote = stockInfo(ticker) ?: continue
            val company = companyProfile(ticker) ?: continue
            val priceChange = calcPriceChangeDailyPrice(stockQuote)
            val percentChange = calcPriceChangeDailyPercentage(
                priceChange,
                stockQuote.currentPrice
            )
            stocks.add(
                Stock(
                    companyName = company.name,
                    ticker = ticker,
                    logo = company.logo,
                    currentPrice = stockQuote.currentPrice,
                    priceChangeDailyPercent = percentChange,
                    priceChangeDailyPrice = priceChange
                )
            )
        }
        return stocks
    }
}