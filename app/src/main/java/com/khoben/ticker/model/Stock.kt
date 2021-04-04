package com.khoben.ticker.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Stock(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val companyName: String,
    val ticker: String,
    var logo: String?,
    val currency: String? = "USD",
    var currentPrice: Double,
    var previousClosePrice: Double? = null,
    var priceChangeDailyPercent: Double,
    var priceChangeDailyPrice: Double,
    var isFavourite: Boolean = false
) : Parcelable

private fun calcPriceChangeDailyPercentage(priceChange: Double, price: Double): Double {
    return priceChange / price * 100.0
}

private fun calcPriceChangeDailyPrice(currentPrice: Double, previousClosePrice: Double): Double {
    return currentPrice - previousClosePrice
}

fun Stock.updateCurrentPrice(currentPrice: Double): Stock {
    if (previousClosePrice == null) return this
    val priceChange = calcPriceChangeDailyPrice(currentPrice, this.previousClosePrice ?: 0.0)
    val percentChange = calcPriceChangeDailyPercentage(
        priceChange,
        this.previousClosePrice!!
    )
    return this.copy(
        currentPrice = currentPrice,
        priceChangeDailyPercent = percentChange,
        priceChangeDailyPrice = priceChange
    )
}
