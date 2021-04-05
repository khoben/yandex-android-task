package com.khoben.ticker.ui

import androidx.lifecycle.*
import com.khoben.ticker.common.SingleLiveData
import com.khoben.ticker.common.onIOLaunch
import com.khoben.ticker.model.*
import com.khoben.ticker.repository.LocalStockRepository
import com.khoben.ticker.repository.RemoteStockRepository
import com.khoben.ticker.repository.WebSocketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import timber.log.Timber

class SharedViewModel(
    private val remoteRepo: RemoteStockRepository,
    private val localRepo: LocalStockRepository,
    private val webSocketRepo: WebSocketRepository
) : ViewModel() {

    private val webSocketSamplePeriodUS = 2000L
    private val initialLoadingItems = 15

    init {
        checkAndFillDatabase()
    }

    val allStocks = localRepo.allStocks()
    val favoriteStocks = localRepo.allFavoriteStocks()

    private val _searchButtonClicked = SingleLiveData<Boolean>()
    val searchButtonClicked: LiveData<Boolean> = _searchButtonClicked

    private val _firstLoadDatabaseStatus = SingleLiveData<FirstLoadStatus>()
    val firstLoadDatabaseStatus: LiveData<FirstLoadStatus> = _firstLoadDatabaseStatus

    private val _stockClicked = SingleLiveData<Stock?>()
    val stockClicked: LiveData<Stock?> = _stockClicked

    private val _watchTicker = MutableLiveData<String>()
    val watchTicker = Transformations.switchMap(_watchTicker) { ticker ->
        localRepo.watchTicker(ticker)
    }

    fun setWatchTicker(ticker: String) {
        _watchTicker.postValue(ticker)
    }

    suspend fun search(query: String) = localRepo.search(query)

    suspend fun candle(ticker: String, period: CandleStockPeriod) =
        remoteRepo.candle(ticker, period)

    suspend fun getCompanyNewsLastWeek(ticker: String): List<News>? {
        val now = Clock.System.now()
        return remoteRepo.getCompanyNewsLastWeek(ticker)
    }

    fun toggleFavorite(ticker: String) {
        viewModelScope.onIOLaunch {
            localRepo.getByTicker(ticker)?.let {
                localRepo.update(it.apply { isFavourite = !isFavourite })
            }
        }
    }

    fun clickedSearchBtn() {
        _searchButtonClicked.value = true
    }

    @FlowPreview
    fun subscribeToSocketEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                localRepo.getAllTickers()?.let { tickerList ->
                    webSocketRepo.startSocket(tickerList)
                        .sample(webSocketSamplePeriodUS)
                        .collect { message ->
                            onWebSocketMessage(message)
                        }
                }
            } catch (ex: Exception) {
                onWebSocketError(ex)
            }
        }
    }

    private suspend fun onWebSocketMessage(message: WebSocketTickerUpdate) {
        message.ticker?.let {
            localRepo.getByTicker(it)?.let { stock ->
                message.price?.let { newPrice ->
                    localRepo.update(stock.updateCurrentPrice(newPrice))
                }
            }
        }
    }

    private fun onWebSocketError(exception: Throwable) {
        Timber.e(exception)
    }

    override fun onCleared() {
        webSocketRepo.stopSocket()
        super.onCleared()
    }

    private fun checkAndFillDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            // if newly created db, fill
            val currentCount = localRepo.countStocks()
            if (currentCount < initialLoadingItems) {
                _firstLoadDatabaseStatus.postValue(FirstLoadStatus.START_LOADING)
                remoteRepo.getFirstSP500(initialLoadingItems - currentCount)?.collect { state ->
                    when (state) {
                        is DataState.Error -> {
                            Timber.e(state.throwable)
                        }
                        is DataState.Loading -> {
                            if (!state.status) {
                                _firstLoadDatabaseStatus.postValue(FirstLoadStatus.LOADED)
                            }
                        }
                        is DataState.Success -> {
                            if (state.data is Stock) {
                                localRepo.insert(state.data)
                            }
                        }
                    }
                }
            } else {
                _firstLoadDatabaseStatus.postValue(FirstLoadStatus.LOADED)
            }
        }
    }

    fun showStock(clicked: Stock) {
        _stockClicked.value = clicked
    }
}
