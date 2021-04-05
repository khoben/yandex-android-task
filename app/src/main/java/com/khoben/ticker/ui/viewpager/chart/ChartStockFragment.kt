package com.khoben.ticker.ui.viewpager.chart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.khoben.ticker.R
import com.khoben.ticker.common.onIOLaunch
import com.khoben.ticker.common.toDataPrice
import com.khoben.ticker.databinding.StockViewChartFragmentBinding
import com.khoben.ticker.model.CandleStockPeriod
import com.khoben.ticker.ui.SharedViewModel
import com.khoben.ticker.ui.StockViewModel
import com.robinhood.spark.SparkView.OnScrubListener
import com.robinhood.spark.animation.LineSparkAnimator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class ChartStockFragment : Fragment() {
    private var _binding: StockViewChartFragmentBinding? = null
    private val binding get() = _binding!!

    private val stockViewModel by sharedViewModel<StockViewModel>()
    private val sharedViewModel by sharedViewModel<SharedViewModel>()

    private var chartAdapter: ChartAdapter? = null

    private val hidingAfter = 500L
    private val scrubTextHidingHandler = Handler(Looper.getMainLooper())
    private val scrubTextHideRunnable = Runnable {
        binding.scrubTextContainer.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StockViewChartFragmentBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // two-way binding
        binding.vm = stockViewModel
        binding.lifecycleOwner = this
        initListeners()
        fillChart(CandleStockPeriod.DAY)
    }

    private fun initListeners() {
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.day_period_btn -> fillChart(CandleStockPeriod.DAY)
                    R.id.week_period_btn -> fillChart(CandleStockPeriod.WEEK)
                    R.id.month_period_btn -> fillChart(CandleStockPeriod.MONTH)
                    R.id.sixmonth_period_btn -> fillChart(CandleStockPeriod.SIXMONTH)
                    R.id.year_period_btn -> fillChart(CandleStockPeriod.ONEYEAR)
                }
            }
        }

        binding.stockChart.apply {
            adapter = ChartAdapter().also { chartAdapter = it }
            sparkAnimator = LineSparkAnimator().apply {
                duration = 1000L
                interpolator = AccelerateDecelerateInterpolator()
            }
            scrubListener = OnScrubListener { value ->
                value?.let { data ->
                    if (data is Pair<*, *>) {
                        binding.scrubTextContainer.visibility = View.VISIBLE
                        binding.chartScrubText.text =
                            (data as Pair<Int, Double>).toDataPrice(stockViewModel.stockData.value?.currency)
                        scrubTextHidingHandler.removeCallbacks(scrubTextHideRunnable)
                        scrubTextHidingHandler.postDelayed(scrubTextHideRunnable, hidingAfter)
                    }
                }
            }
        }
    }

    private fun fillChart(type: CandleStockPeriod) {
        stockViewModel.stockData.value?.ticker?.let { ticker ->
            lifecycleScope.onIOLaunch {
                val data = sharedViewModel.candle(ticker, type)
                Timber.d(data.toString())
                if (data != null) {
                    if (data.prices == null || data.timestamps == null) {
                        this@ChartStockFragment.activity?.runOnUiThread {
                            Toast.makeText(context, "No data for this timeframe", Toast.LENGTH_LONG)
                                .show()
                            chartAdapter?.setData(null)
                        }
                    } else {
                        this@ChartStockFragment.activity?.runOnUiThread {
                            chartAdapter?.setData(data)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scrubTextHidingHandler.removeCallbacks(scrubTextHideRunnable)
    }
}