package com.khoben.ticker.ui

import android.content.Context
import android.os.Environment
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.khoben.ticker.common.ImageRemoteDownloader
import com.khoben.ticker.common.onIOLaunch
import com.khoben.ticker.database.AppDatabase
import com.khoben.ticker.model.Stock
import com.khoben.ticker.repository.FinnhubRepository

class SharedViewModel(
    private val context: Context,
    private val repo: FinnhubRepository,
    db: AppDatabase
) : ViewModel() {
    private val stockDao = db.stockDao()

    val stockList: LiveData<List<Stock>>? = stockDao.all

    private val _searchButtonClicked = MutableLiveData<Boolean>()
    val searchButtonClicked: LiveData<Boolean> = _searchButtonClicked

    val favoriteStocks: LiveData<List<Stock>>? = stockDao.favorite

    private var _firstLoadDatabaseStatus = MutableLiveData<FirstLoadStatus>()
    val firstLoadDatabaseStatus: LiveData<FirstLoadStatus> = _firstLoadDatabaseStatus

    fun checkAndFillDatabase() {
        viewModelScope.onIOLaunch {
            val data = stockDao.all()
            // if newly created db, fill
            if (data == null || data.isEmpty()) {
                _firstLoadDatabaseStatus.postValue(FirstLoadStatus.START_LOADING)
                val stocks = repo.searchStocks()
                stocks.forEach {
                    if (!it.logo.isNullOrBlank() && it.logo!!.isNotEmpty()) {
                        val request = ImageRequest.Builder(context)
                            .data(it.logo)
                            .build()
                        val drawable = ImageLoader(context).execute(request).drawable
                        val outputDir =
                            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath!!
                        val logo = ImageRemoteDownloader.saveImage(
                            drawable!!.toBitmap(),
                            outputDir,
                            it.ticker
                        )
                        stockDao.insert(it.copy(logo = logo?.absolutePath))
                    } else {
                        stockDao.insert(it.copy(logo = ""))
                    }
                }
            }
            _firstLoadDatabaseStatus.postValue(FirstLoadStatus.LOADED)
        }
    }

    fun toggleFavorite(ticker: String) {
        viewModelScope.onIOLaunch {
            stockDao.getByTicker(ticker)?.let {
                stockDao.update(
                    it.apply {
                        isFavourite = !isFavourite
                    }
                )
            }
        }
    }

    fun clickedSearchBtn() {
        _searchButtonClicked.value = true
    }

    fun search(query: String) = stockDao.searchDatabase(query)
}