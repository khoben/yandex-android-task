package com.khoben.ticker.ui.viewpager.chart

import android.widget.Toast
import com.khoben.ticker.model.CandleStock
import com.robinhood.spark.SparkAdapter


class ChartAdapter : SparkAdapter() {

    private var data: CandleStock? = null

    fun setData(data: CandleStock?) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getCount() = data?.prices?.size ?: 0

    override fun getItem(index: Int) = Pair(data?.timestamps?.get(index), data?.prices?.get(index))

    override fun getY(index: Int) = data?.prices?.get(index)?.toFloat() ?: 0F
}