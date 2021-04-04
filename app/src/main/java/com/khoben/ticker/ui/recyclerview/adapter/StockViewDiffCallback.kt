package com.khoben.ticker.ui.recyclerview.adapter

import androidx.recyclerview.widget.DiffUtil
import com.khoben.ticker.model.Stock
import timber.log.Timber

class StockViewDiffCallback : DiffUtil.ItemCallback<Stock>() {
    override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return (oldItem.ticker == newItem.ticker)
    }

    override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return (oldItem == newItem)
    }

}
