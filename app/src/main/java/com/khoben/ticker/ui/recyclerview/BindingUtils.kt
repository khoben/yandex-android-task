package com.khoben.ticker.ui.recyclerview

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.khoben.ticker.R
import com.khoben.ticker.common.Utils
import com.khoben.ticker.common.format
import com.khoben.ticker.common.toCurrencySign
import com.khoben.ticker.model.Stock
import timber.log.Timber

@SuppressLint("SetTextI18n")
@BindingAdapter("current_price")
fun TextView.setCurrentPrice(stock: Stock) {
    text = "${stock.currency?.toCurrencySign()}${stock.currentPrice}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("daily_price_change")
fun TextView.setDailyPriceChange(stock: Stock) {
    when {
        stock.priceChangeDailyPercent < 0 -> {
            text =
                "${stock.priceChangeDailyPrice.format(2)} (${stock.priceChangeDailyPercent.format(2)}%)"
            setTextColor(ContextCompat.getColor(context, R.color.negative_change_color))
        }
        stock.priceChangeDailyPercent > 0 -> {
            text = "+${stock.priceChangeDailyPrice.format(2)} (+${
                stock.priceChangeDailyPercent.format(2)
            }%)"
            setTextColor(ContextCompat.getColor(context, R.color.positive_change_color))
        }
        else -> {
            text =
                "${stock.priceChangeDailyPrice.format(2)} (${stock.priceChangeDailyPercent.format(2)}%)"
        }
    }
}

fun ImageView.setImage(path: String?) {
    if (path != null && Utils.checkIfExists(path)) {
        setImageBitmap(BitmapFactory.decodeFile(path))
    } else {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.image_placeholder))
        Timber.e("Trying to load not existing thumb")
    }
}