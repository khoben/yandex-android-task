package com.khoben.ticker.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.khoben.ticker.databinding.MainFragmentBinding
import com.khoben.ticker.model.Stock
import com.khoben.ticker.repository.FinnhubRepository
import com.khoben.ticker.ui.recyclerview.FavoriteStockFragment
import com.khoben.ticker.ui.recyclerview.adapter.StockViewListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding

    private val sharedViewModel by sharedViewModel<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        binding.root.post {
            initViewPager()
        }
    }

    private val titleTabs = listOf("Stocks", "Favourite")

    private fun initViewPager() {
        binding.viewPager.adapter = StockViewPagerAdapter(this).apply {
            add(MainStockFragment(), 0)
            add(FavoriteStockFragment(), 1)
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titleTabs[position]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setStyleForTab(tab, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                setStyleForTab(tab, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            fun setStyleForTab(tab: TabLayout.Tab, style: Int) {
                tab.view.children.find { it is TextView }?.let { tv ->
                    (tv as TextView).post {
                        tv.setTypeface(null, style)
                    }
                }
            }
        })
    }

    private fun initClickListeners() {
        binding.searchField.setOnClickListener {
            sharedViewModel.clickedSearchBtn()
        }
    }

}
