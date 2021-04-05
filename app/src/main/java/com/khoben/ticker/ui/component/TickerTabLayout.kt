package com.khoben.ticker.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.khoben.ticker.R

class TickerTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr) {

    init {
        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                changeSelection(tab, true)
            }

            override fun onTabUnselected(tab: Tab) {
                changeSelection(tab, false)
            }

            override fun onTabReselected(tab: Tab) {
            }
        })
    }

    private fun changeSelection(tab: Tab, selected: Boolean) {
        (tab.customView as TextView?)?.let { tv ->
            if (selected) {
                TextViewCompat.setTextAppearance(tv, R.style.TickerAppTabStyle_Selected)
            } else {
                TextViewCompat.setTextAppearance(tv, R.style.TickerAppTabStyle)
            }
        }
    }

    fun setupViewPager(viewPager: ViewPager2, titles: List<String>) {
        TabLayoutMediator(this, viewPager) { tab, position ->
            tab.customView =
                LayoutInflater.from(context).inflate(R.layout.custom_tablayout_tab, null)
            tab.text = titles[position]
            if (position == 0) {
                viewPager.currentItem = 0
            }
        }.attach()
    }
}