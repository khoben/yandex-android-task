package com.khoben.ticker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.SimpleItemAnimator
import com.khoben.ticker.databinding.MainFragmentBinding
import com.khoben.ticker.databinding.ViewpagerStockListBinding
import com.khoben.ticker.model.Stock
import com.khoben.ticker.ui.recyclerview.adapter.StockViewListAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class MainStockFragment : Fragment(), StockViewListAdapter.StockClickListener {
    private lateinit var binding: ViewpagerStockListBinding
    private lateinit var recyclerViewAdapter: StockViewListAdapter

    private val sharedViewModel by sharedViewModel<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewpagerStockListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservables()
    }

    private fun initRecyclerView() {
        (binding.stocks.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.stocks.itemAnimator = null
        binding.stocks.setItemViewCacheSize(20)
        binding.stocks.isDrawingCacheEnabled = true
        binding.stocks.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        recyclerViewAdapter = StockViewListAdapter().apply {
            stockClickListener = this@MainStockFragment
        }
        binding.stocks.adapter = recyclerViewAdapter
    }

    private fun initObservables() {
        sharedViewModel.stockList?.observe(viewLifecycleOwner, {
            submitList(it)
        })
    }

    private fun submitList(stocks: List<Stock>) {
        recyclerViewAdapter.submitList(stocks)
    }

    override fun onStockClick(ticker: String) {

    }

    override fun onFavoriteClick(ticker: String) {
        sharedViewModel.toggleFavorite(ticker)
    }
}