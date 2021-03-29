package com.khoben.ticker.ui.recyclerview.adapter

import androidx.recyclerview.widget.DiffUtil
import com.khoben.ticker.model.Stock

class StockViewDiffCallback : DiffUtil.ItemCallback<Stock>() {
    override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem.ticker == newItem.ticker
    }

    override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem.ticker == newItem.ticker &&
                oldItem.companyName == newItem.companyName &&
                oldItem.currentPrice == newItem.currentPrice &&
                oldItem.logo == newItem.logo &&
                oldItem.priceChangeDailyPercent == newItem.priceChangeDailyPercent &&
                oldItem.isFavourite == newItem.isFavourite
    }

}
