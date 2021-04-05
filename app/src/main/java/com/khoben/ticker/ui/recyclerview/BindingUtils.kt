package com.khoben.ticker.ui.recyclerview

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.khoben.ticker.R
import com.khoben.ticker.common.format
import com.khoben.ticker.common.toCurrencySign
import com.khoben.ticker.model.Stock

@SuppressLint("SetTextI18n")
@BindingAdapter("current_price")
fun TextView.setCurrentPrice(stock: Stock?) {
    if (stock == null) return
    text = "${stock.currency?.toCurrencySign()}${stock.currentPrice}"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("daily_price_change")
fun TextView.setDailyPriceChange(stock: Stock?) {
    if (stock == null) return
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

@BindingAdapter("is_favourite")
fun MaterialButton.isFavourite(isFavourite: Boolean) {
    if (isFavourite) setIconTintResource(R.color.favorite_active_color)
    else setIconTintResource(R.color.favorite_inactive_color)
}