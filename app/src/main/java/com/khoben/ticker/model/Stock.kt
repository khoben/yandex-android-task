package com.khoben.ticker.model

import android.content.Context
import android.os.Parcelable
import android.webkit.URLUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.khoben.ticker.common.ImageRemoteDownloader
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
    var priceChangeDailyPercent: Double,
    var priceChangeDailyPrice: Double,
    var isFavourite: Boolean = false
) : Parcelable
