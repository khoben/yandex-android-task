package com.khoben.ticker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialSharedAxis
import com.khoben.ticker.R
import com.khoben.ticker.databinding.StockViewFragmentBinding
import com.khoben.ticker.model.Stock
import com.khoben.ticker.ui.viewpager.FragmentViewPagerAdapter
import com.khoben.ticker.ui.viewpager.chart.ChartStockFragment
import com.khoben.ticker.ui.viewpager.favourite.FavoriteStockFragment
import com.khoben.ticker.ui.viewpager.main.MainStockFragment
import com.khoben.ticker.ui.viewpager.news.NewsStockFragment
import com.khoben.ticker.ui.viewpager.summary.SummaryStockFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class StockViewFragment : Fragment() {
    private var _binding: StockViewFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel by sharedViewModel<SharedViewModel>()
    private val stockViewModel by sharedViewModel<StockViewModel>()

    private var currentStock: Stock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply { duration = 300 }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StockViewFragmentBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Stock>(EXTRA_STOCK)?.let { stock ->
            currentStock = stock
        }
        initListeners()
        initViewPager()
        initObservables()

        // two-way binding
        currentStock?.let { stock ->
            stockViewModel.setStock(stock)
            binding.lifecycleOwner = this
            binding.vm = stockViewModel
        }

        // marque
        binding.companyName.isSelected = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObservables() {
        currentStock?.let { sharedViewModel.setWatchTicker(it.ticker) }
        sharedViewModel.watchTicker.observe(viewLifecycleOwner, { stock ->
            stock?.let { stockViewModel.setStock(it) }
        })
    }

    private fun initListeners() {
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.favouriteBtn.setOnClickListener {
            currentStock?.let { stock ->
                sharedViewModel.toggleFavorite(stock.ticker)
            }
        }
    }

    private val titleTabs = listOf("Chart", "Summary", "News")

    private fun initViewPager() {
        binding.viewPager.adapter = FragmentViewPagerAdapter(this).apply {
            add(ChartStockFragment())
            add(SummaryStockFragment())
            add(NewsStockFragment())
        }
        binding.tabLayout.setupViewPager(binding.viewPager, titleTabs)
    }

    companion object {
        val TAG = StockViewFragment::class.java.simpleName
        private val EXTRA_STOCK = "extra_stock"

        @JvmOverloads
        fun show(stock: Stock? = null): StockViewFragment {
            return StockViewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_STOCK, stock)
                }
            }
        }
    }
}