package com.khoben.ticker.di.module

import com.khoben.ticker.ui.SharedViewModel
import com.khoben.ticker.ui.StockViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SharedViewModel(get(), get(), get())
    }
    viewModel {
        StockViewModel()
    }
}