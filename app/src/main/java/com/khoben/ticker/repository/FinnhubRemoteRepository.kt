package com.khoben.ticker.repository

import com.khoben.ticker.api.finnhub.http.FinnHubService
import com.khoben.ticker.api.finnhub.http.model.StockInfoResult
import com.khoben.ticker.common.ApiErrorProvider
import com.khoben.ticker.common.formatDate
import com.khoben.ticker.common.toModel
import com.khoben.ticker.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

class FinnhubRemoteRepository(private val service: FinnHubService) : RemoteStockRepository {

    private inline fun <T> safeApiCall(responseFunction: () -> T): T? {
        return try {
            responseFunction.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
            ApiErrorProvider.postValue(e)
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
        return priceChange / currentPrice * 100.0
    }

    private fun calcPriceChangeDailyPrice(currentPrice: Double, previousClosePrice: Double): Double {
        return currentPrice - previousClosePrice
    }

    override suspend fun getFirstSP500(amount: Int): Flow<DataState<Stock>> = flow {
        emit(DataState.Loading(true))
        val allStocks = sp500()?.constituents?.take(amount) ?: emptyList()
        // getting some stocks
        try {
            for (ticker in allStocks) {
                val stockQuote = stockInfo(ticker) ?: continue
                val company = companyProfile(ticker) ?: continue
                val priceChange = calcPriceChangeDailyPrice(stockQuote.currentPrice, stockQuote.previousClosePrice)
                val percentChange = calcPriceChangeDailyPercentage(
                    priceChange,
                    stockQuote.previousClosePrice
                )
                emit(
                    DataState.Success(
                        Stock(
                            companyName = company.name,
                            ticker = ticker,
                            logo = company.logo,
                            currentPrice = stockQuote.currentPrice,
                            previousClosePrice = stockQuote.previousClosePrice,
                            priceChangeDailyPercent = percentChange,
                            priceChangeDailyPrice = priceChange
                        )
                    )
                )
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
        emit(DataState.Loading(false))
    }

    override suspend fun getCompanyNewsLastWeek(ticker: String): List<News>? {
        val now = Clock.System.now()
        return service.companyNews(
            ticker, now.minus(1, DateTimeUnit.WEEK, TimeZone.currentSystemDefault())
                .toEpochMilliseconds().formatDate("YYYY-MM-dd"),
            now.toEpochMilliseconds().formatDate("YYYY-MM-dd")
        )?.map { item -> item.toModel() }
    }

    override suspend fun candle(symbol: String, period: CandleStockPeriod): CandleStock? {
        val now = Clock.System.now()
        return when (period) {
            CandleStockPeriod.DAY -> {
                candleDay(
                    symbol,
                    now.minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                        .epochSeconds,
                    now.epochSeconds
                )
            }
            CandleStockPeriod.WEEK -> {
                candleWeek(
                    symbol,
                    now.minus(1, DateTimeUnit.WEEK, TimeZone.currentSystemDefault())
                        .epochSeconds, now.epochSeconds
                )
            }
            CandleStockPeriod.MONTH -> {
                candleMonth(
                    symbol,
                    now.minus(1, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
                        .epochSeconds, now.epochSeconds
                )
            }
            CandleStockPeriod.SIXMONTH -> {
                candleSixMonth(
                    symbol,
                    now.minus(6, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
                        .epochSeconds, now.epochSeconds
                )
            }
            CandleStockPeriod.ONEYEAR -> {
                candleOneYear(
                    symbol,
                    now.minus(1, DateTimeUnit.YEAR, TimeZone.currentSystemDefault())
                        .epochSeconds, now.epochSeconds
                )
            }
        }
    }

    private suspend fun candleDay(symbol: String, from: Long, to: Long): CandleStock? =
        safeApiCall {
            service.candle(symbol, 60.toString(), from, to).toModel()
        }

    private suspend fun candleWeek(symbol: String, from: Long, to: Long): CandleStock? =
        safeApiCall {
            service.candle(symbol, 60.toString(), from, to).toModel()
        }

    private suspend fun candleMonth(symbol: String, from: Long, to: Long): CandleStock? =
        safeApiCall {
            service.candle(symbol, "D", from, to).toModel()

        }

    private suspend fun candleSixMonth(symbol: String, from: Long, to: Long): CandleStock? =
        safeApiCall {
            service.candle(symbol, "W", from, to).toModel()

        }

    private suspend fun candleOneYear(symbol: String, from: Long, to: Long) = safeApiCall {
        service.candle(symbol, "W", from, to).toModel()
    }

}