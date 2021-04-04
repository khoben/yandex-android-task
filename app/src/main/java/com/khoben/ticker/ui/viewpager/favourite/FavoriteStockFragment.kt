package com.khoben.ticker.ui.viewpager.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.SimpleItemAnimator
import com.khoben.ticker.databinding.MainFragmentBinding
import com.khoben.ticker.databinding.ViewpagerStockListBinding
import com.khoben.ticker.model.Stock
import com.khoben.ticker.ui.SharedViewModel
import com.khoben.ticker.ui.recyclerview.adapter.StockViewListAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FavoriteStockFragment : Fragment() {
    private var _binding: ViewpagerStockListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewAdapter: StockViewListAdapter

    private val sharedViewModel by sharedViewModel<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewpagerStockListBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservables()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        recyclerViewAdapter = StockViewListAdapter({ clickedItem, idx ->
            sharedViewModel.showStock(clickedItem)
        }, { favouriteToggledItem, idx ->
            sharedViewModel.toggleFavorite(favouriteToggledItem.ticker)
        })
        binding.stocks.adapter = recyclerViewAdapter
        binding.stocks.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun initObservables() {
        sharedViewModel.favoriteStocks?.observe(viewLifecycleOwner, {
            submitList(it)
        })
    }

    private fun submitList(stocks: List<Stock>) {
        recyclerViewAdapter.submitList(stocks)
    }
}