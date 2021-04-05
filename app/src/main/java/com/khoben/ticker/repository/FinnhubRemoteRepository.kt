package com.khoben.ticker.repository

import com.khoben.ticker.api.finnhub.http.FinnHubService
import com.khoben.ticker.common.formatDate
import com.khoben.ticker.common.toModel
import com.khoben.ticker.model.CandleStockPeriod
import com.khoben.ticker.model.DataState
import com.khoben.ticker.model.Stock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import timber.log.Timber

class FinnhubRemoteRepository(private val service: FinnHubService) : RemoteStockRepository {

    private suspend inline fun <T> safeApiCall(crossinline body: suspend () -> Flow<DataState<T>>) =
        callbackFlow {
            offer(DataState.Loading(true))
            body().collect {
                Timber.d("$it")
                offer(it)
            }
            offer(DataState.Loading(false))
            awaitClose()
        }.catch { e ->
            emit(DataState.Error(e))
        }

    private suspend fun search(query: String) = service.search(query)
    private suspend fun sp500() = service.sp500()
    private suspend fun allStockExchange() = service.allStockExchange()
    private suspend fun companyProfile(symbol: String) = service.companyProfile(symbol)

    private suspend fun stockInfo(symbol: String) = service.stockInfo(symbol)
    private suspend fun companyNews(symbol: String, from: String, to: String) =
        service.companyNews(symbol, from, to)

    private fun calcPriceChangeDailyPercentage(priceChange: Double, currentPrice: Double): Double {
        return priceChange / currentPrice * 100.0
    }

    private fun calcPriceChangeDailyPrice(
        currentPrice: Double,
        previousClosePrice: Double
    ): Double {
        return currentPrice - previousClosePrice
    }

    @ExperimentalCoroutinesApi
    override suspend fun getFirstSP500(amount: Int) =
        safeApiCall {
            flow {
                val allStocks = sp500().constituents.take(amount)
                // getting some stocks
                for (ticker in allStocks) {
                    val stockQuote = stockInfo(ticker)
                    val company = companyProfile(ticker)
                    val priceChange = calcPriceChangeDailyPrice(
                        stockQuote.currentPrice,
                        stockQuote.previousClosePrice
                    )
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
            }
        }

    @ExperimentalCoroutinesApi
    override suspend fun getCompanyNewsLastWeek(ticker: String) =
        safeApiCall {
            flow {
                val now = Clock.System.now()
                service.companyNews(
                    ticker, now.minus(1, DateTimeUnit.WEEK, TimeZone.currentSystemDefault())
                        .toEpochMilliseconds().formatDate("YYYY-MM-dd"),
                    now.toEpochMilliseconds().formatDate("YYYY-MM-dd")
                )?.map { item -> item.toModel() }?.forEach { news ->
                    emit((DataState.Success(news)))
                }
            }
        }


    @ExperimentalCoroutinesApi
    override suspend fun candle(symbol: String, period: CandleStockPeriod) =
        safeApiCall {
            flow {
                val now = Clock.System.now()
                when (period) {
                    CandleStockPeriod.DAY -> {
                        emit(
                            DataState.Success(
                                candleDay(
                                    symbol,
                                    now.minus(
                                        1,
                                        DateTimeUnit.DAY,
                                        TimeZone.currentSystemDefault()
                                    ).epochSeconds,
                                    now.epochSeconds
                                )
                            )
                        )
                    }
                    CandleStockPeriod.WEEK -> {
                        emit(
                            DataState.Success(
                                candleWeek(
                                    symbol,
                                    now.minus(
                                        1,
                                        DateTimeUnit.WEEK,
                                        TimeZone.currentSystemDefault()
                                    )
                                        .epochSeconds, now.epochSeconds
                                )
                            )
                        )
                    }
                    CandleStockPeriod.MONTH -> {
                        emit(
                            DataState.Success(
                                candleMonth(
                                    symbol,
                                    now.minus(
                                        1,
                                        DateTimeUnit.MONTH,
                                        TimeZone.currentSystemDefault()
                                    )
                                        .epochSeconds, now.epochSeconds
                                )
                            )
                        )
                    }
                    CandleStockPeriod.SIXMONTH -> {
                        emit(
                            DataState.Success(
                                candleSixMonth(
                                    symbol,
                                    now.minus(
                                        6,
                                        DateTimeUnit.MONTH,
                                        TimeZone.currentSystemDefault()
                                    )
                                        .epochSeconds, now.epochSeconds
                                )
                            )
                        )
                    }
                    CandleStockPeriod.ONEYEAR -> {
                        emit(
                            DataState.Success(
                                candleOneYear(
                                    symbol,
                                    now.minus(
                                        1,
                                        DateTimeUnit.YEAR,
                                        TimeZone.currentSystemDefault()
                                    ).epochSeconds, now.epochSeconds
                                )
                            )
                        )
                    }
                }
            }
        }


    private suspend fun candleDay(symbol: String, from: Long, to: Long) =
        service.candle(symbol, 60.toString(), from, to).toModel()

    private suspend fun candleWeek(symbol: String, from: Long, to: Long) =
        service.candle(symbol, 60.toString(), from, to).toModel()

    private suspend fun candleMonth(symbol: String, from: Long, to: Long) =
        service.candle(symbol, "D", from, to).toModel()

    private suspend fun candleSixMonth(symbol: String, from: Long, to: Long) =
        service.candle(symbol, "W", from, to).toModel()

    private suspend fun candleOneYear(symbol: String, from: Long, to: Long) =
        service.candle(symbol, "W", from, to).toModel()
}