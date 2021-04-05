package com.khoben.ticker.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.khoben.ticker.R
import com.khoben.ticker.common.ApiErrorProvider
import com.khoben.ticker.common.ConnectivityProvider
import com.khoben.ticker.databinding.ActivityMainBinding
import com.khoben.ticker.model.FirstLoadStatus
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val sharedViewModel by viewModel<SharedViewModel>()

    private val currentFragment get() = supportFragmentManager.fragments.lastOrNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAndShowFragment()
        initObservables()
    }

    private fun initObservables() {
        sharedViewModel.searchButtonClicked.observe(this, { clicked ->
            if (clicked) {
                val searchFragment =
                    supportFragmentManager.findFragmentByTag("search") ?: SearchFragment()

                if (!searchFragment.isAdded) {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.activity_main_container, searchFragment, "search")
                        .addToBackStack(null)
                        .commit()
                }
            }
        })
        sharedViewModel.firstLoadDatabaseStatus.observe(this, { loadingStatus ->
            when (loadingStatus) {
                FirstLoadStatus.START_LOADING -> {
                    binding.loading.root.visibility = View.VISIBLE
                }
                FirstLoadStatus.LOADED -> {
                    binding.loading.root.visibility = View.GONE
                    sharedViewModel.subscribeToSocketEvents()
                }
            }
        })
        sharedViewModel.stockClicked.observe(this, { clickedStock ->
            if (clickedStock != null) {
                val stockFragment = supportFragmentManager.findFragmentByTag(StockViewFragment.TAG)
                    ?: StockViewFragment.show(clickedStock)
                if (!stockFragment.isAdded) {
                    supportFragmentManager.beginTransaction()
                        .also { ft ->
                            currentFragment?.let { ft.setMaxLifecycle(it, Lifecycle.State.STARTED) }
                        }
                        .add(
                            R.id.activity_main_container,
                            stockFragment,
                            StockViewFragment.TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
        })
        ConnectivityProvider.observe(this, { hasConnection ->
            if (hasConnection)
                binding.noInternet.visibility = View.GONE
            else
                binding.noInternet.visibility = View.VISIBLE
        })

        ApiErrorProvider.init().observe(this, { apiError ->
            Timber.d(apiError)
            runOnUiThread {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "API Error: ${apiError?.localizedMessage}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun initAndShowFragment() {
        if (supportFragmentManager.findFragmentByTag("main") == null) {
            val mainFragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.activity_main_container, mainFragment, "main")
                .commit()
        }
    }
}