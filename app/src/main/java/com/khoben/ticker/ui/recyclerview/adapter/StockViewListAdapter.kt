package com.khoben.ticker.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.khoben.ticker.R
import com.khoben.ticker.databinding.TickerRecyclerviewLayoutBinding
import com.khoben.ticker.model.Stock
import java.io.File

class StockViewListAdapter(
    private val onItemClicked: ((Stock, Int) -> Unit)? = null,
    private val onFavouriteClick: ((Stock, Int) -> Unit)? = null
) : ListAdapter<Stock, StockViewListAdapter.StockViewHolder>(StockViewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TickerRecyclerviewLayoutBinding.inflate(layoutInflater, parent, false)
        return StockViewHolder(
            binding,
            { onItemClicked?.invoke(getItem(it), it) },
            { onFavouriteClick?.invoke(getItem(it), it) })
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun toggleFavouriteState(idx: Int) {
        val item = currentList[idx]
        item.isFavourite = !item.isFavourite
        notifyItemChanged(idx)
    }

    inner class StockViewHolder(
        private val binding: TickerRecyclerviewLayoutBinding,
        private val onItemClicked: ((Int) -> Unit)? = null,
        private val onFavouriteClick: ((Int) -> Unit)? = null
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val evenItemColor = ContextCompat.getColor(
            itemView.context,
            R.color.even_item_color
        )

        private val oddItemColor = ContextCompat.getColor(
            itemView.context,
            R.color.odd_item_color
        )

        init {
            itemView.setOnClickListener {
                onItemClicked?.invoke(adapterPosition)
            }
            binding.favouriteBtn.setOnClickListener {
                onFavouriteClick?.invoke(adapterPosition)
            }
        }

        fun bind(stock: Stock) {
            binding.stock = stock
            binding.executePendingBindings()

            // makes marque works
            binding.companyName.isSelected = true

            if (adapterPosition % 2 != 0) {
                binding.stockCard.setCardBackgroundColor(evenItemColor)
            } else {
                binding.stockCard.setCardBackgroundColor(oddItemColor)
            }

            binding.stockLogo.load(File(stock.logo ?: "")) {
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.image_placeholder)
            }
        }
    }
}