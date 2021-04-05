package com.khoben.ticker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialFadeThrough
import com.khoben.ticker.databinding.MainFragmentBinding
import com.khoben.ticker.model.FirstLoadStatus
import com.khoben.ticker.ui.viewpager.FragmentViewPagerAdapter
import com.khoben.ticker.ui.viewpager.favourite.FavoriteStockFragment
import com.khoben.ticker.ui.viewpager.main.MainStockFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel by sharedViewModel<SharedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough().apply { duration = 300 }
        reenterTransition = MaterialFadeThrough().apply { duration = 300 }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initViewPager()
        initObservables()
    }

    private fun initObservables() {
        sharedViewModel.firstLoadDatabaseStatus.observe(viewLifecycleOwner, { loadingStatus ->
            when (loadingStatus) {
                FirstLoadStatus.START_LOADING -> {
                    binding.loading.root.visibility = View.VISIBLE
                }
                FirstLoadStatus.LOADED -> {
                    binding.loading.root.visibility = View.GONE
                    sharedViewModel.subscribeToSocketEvents()
                }
                null -> {
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val titleTabs = listOf("Stocks", "Favourite")

    private fun initViewPager() {
        binding.viewPager.adapter = FragmentViewPagerAdapter(this).apply {
            add(MainStockFragment())
            add(FavoriteStockFragment())
        }
        binding.tabLayout.setupViewPager(binding.viewPager, titleTabs)
    }


    private fun initClickListeners() {
        binding.searchField.setOnClickListener {
            sharedViewModel.clickedSearchBtn()
        }
    }

}
