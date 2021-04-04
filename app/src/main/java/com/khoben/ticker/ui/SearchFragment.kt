package com.khoben.ticker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.transition.MaterialFadeThrough
import com.khoben.ticker.common.Utils.focusAndShowKeyboard
import com.khoben.ticker.common.Utils.hideKeyboard
import com.khoben.ticker.databinding.SearchFragmentBinding
import com.khoben.ticker.model.Stock
import com.khoben.ticker.ui.component.EditTextWithControls
import com.khoben.ticker.ui.recyclerview.adapter.StockViewListAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SearchFragment : Fragment() {
    private val sharedViewModel by sharedViewModel<SharedViewModel>()

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapter: StockViewListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply { duration = 300 }
        returnTransition = MaterialFadeThrough().apply { duration = 300 }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        binding.searchField.focusAndShowKeyboard()
    }

    override fun onPause() {
        super.onPause()
        binding.searchField.hideKeyboard()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListeners() {
        binding.searchField.listener = object : EditTextWithControls.EditTextWithControlsListener {
            override fun onBackClicked() {
                parentFragmentManager.popBackStack()
            }

            override fun onTextChanged(text: CharSequence) {
                trySearchStock(text.toString())
            }

            override fun onFocusChanged(focused: Boolean) {
            }
        }
    }

    private fun trySearchStock(query: String) {
        lifecycleScope.launch {
            sharedViewModel.search(query)?.let { data ->
                submitList(data)
            }
        }
    }

    private fun initRecyclerView() {
        recyclerViewAdapter = StockViewListAdapter({ clickedItem, idx ->
            sharedViewModel.showStock(clickedItem)
        }, { favouriteToggledItem, idx ->
            sharedViewModel.toggleFavorite(favouriteToggledItem.ticker)
            // changing ui state as it is just single request (handling favourite btn click)
            recyclerViewAdapter.toggleFavouriteState(idx)
        })
        binding.resultStocks.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        binding.resultStocks.adapter = recyclerViewAdapter
    }

    private fun submitList(stocks: List<Stock>) {
        recyclerViewAdapter.submitList(stocks)
    }

}
