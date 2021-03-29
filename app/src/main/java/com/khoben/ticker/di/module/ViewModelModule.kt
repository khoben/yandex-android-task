package com.khoben.ticker.di.module

import com.khoben.ticker.ui.SharedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SharedViewModel(androidContext(), get(), get())
    }
}