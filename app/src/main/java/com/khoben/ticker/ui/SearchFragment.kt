package com.khoben.ticker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.khoben.ticker.common.Utils.focusAndShowKeyboard
import com.khoben.ticker.common.Utils.hideKeyboard
import com.khoben.ticker.databinding.SearchFragmentBinding
import com.khoben.ticker.model.Stock
import com.khoben.ticker.ui.recyclerview.adapter.StockViewListAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class SearchFragment : Fragment(), StockViewListAdapter.StockClickListener {
    private lateinit var binding: SearchFragmentBinding
    private val sharedViewModel by sharedViewModel<SharedViewModel>()
    private lateinit var adapter: StockViewListAdapter

    val DRAWABLE_LEFT = 0
    val DRAWABLE_TOP = 1
    val DRAWABLE_RIGHT = 2
    val DRAWABLE_BOTTOM = 3

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        // lose focus on edittext then touch outside
        binding.root.setOnTouchListener { _, _ ->
            Timber.d("toUC")
            binding.searchField.clearFocus()
            binding.searchField.hideKeyboard()
            false
        }
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchField.setOnTouchListener(OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // right icon (clear text)
                if (event.rawX >= binding.searchField.right - 1.5 * binding.searchField.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                    && binding.searchField.compoundDrawables[DRAWABLE_RIGHT].alpha > 0
                ) {
                    binding.searchField.text.clear()
                    return@OnTouchListener true
                    // left icon (back)
                } else if (event.rawX <= binding.searchField.left + 1.5 * binding.searchField.compoundDrawables[DRAWABLE_LEFT].bounds.width()) {
                    activity?.onBackPressed()
                    return@OnTouchListener true
                }
            }
            false
        })
        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    trySearchStock(s.toString())
                    binding.searchField.compoundDrawables[DRAWABLE_RIGHT].alpha = 255
                } else {
                    binding.searchField.compoundDrawables[DRAWABLE_RIGHT].alpha = 0
                    submitList(emptyList())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.searchField.focusAndShowKeyboard()
        initRecyclerView()
        binding.searchField.post {
            binding.searchField.compoundDrawables[DRAWABLE_RIGHT].alpha = 0
        }
    }

    private fun trySearchStock(query: String) {
        sharedViewModel.search(query)?.observe(this, {
            submitList(it)
        })
    }

    private fun initRecyclerView() {
        adapter = StockViewListAdapter()
        binding.resultStocks.adapter = adapter.apply {
            stockClickListener = this@SearchFragment
        }
    }


    private fun submitList(stocks: List<Stock>) {
        adapter.submitList(stocks)
    }

    override fun onStockClick(ticker: String) {
    }

    override fun onFavoriteClick(ticker: String) {
        sharedViewModel.toggleFavorite(ticker)
    }

}
