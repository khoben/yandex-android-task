package com.khoben.ticker.ui.recyclerview.adapter

import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.button.MaterialButton
import com.khoben.ticker.R
import com.khoben.ticker.databinding.TickerRecyclerviewLayoutBinding
import com.khoben.ticker.model.Stock
import com.khoben.ticker.ui.recyclerview.setImage

class StockViewListAdapter :
    ListAdapter<Stock, StockViewListAdapter.StockViewHolder>(StockViewDiffCallback()) {

    var stockClickListener: StockClickListener? = null

    interface StockClickListener {
        fun onStockClick(ticker: String)
        fun onFavoriteClick(ticker: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TickerRecyclerviewLayoutBinding.inflate(layoutInflater, parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class StockViewHolder(private val binding: TickerRecyclerviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: Stock) {
            binding.stock = stock
            binding.executePendingBindings()

            binding.companyName.marqueeRepeatLimit = 2
            binding.companyName.setHorizontallyScrolling(true)
            binding.companyName.isSingleLine = true
            binding.companyName.ellipsize = TextUtils.TruncateAt.MARQUEE
            binding.companyName.isSelected = true

            if (adapterPosition % 2 != 0) {
                binding.stockCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.even_item_color
                    )
                )
            } else {
                binding.stockCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.odd_item_color
                    )
                )
            }

            if (stock.isFavourite) {
                (binding.favouriteBtn as MaterialButton).setIconTintResource(R.color.favorite_active_color)
            } else {
                (binding.favouriteBtn as MaterialButton).setIconTintResource(R.color.favorite_inactive_color)
            }

            binding.root.setOnClickListener {
                stockClickListener?.onStockClick(stock.ticker)
            }
            binding.favouriteBtn.setOnClickListener {
                stockClickListener?.onFavoriteClick(stock.ticker)
            }

            binding.stockLogo.setImage(stock.logo)
        }
    }
}