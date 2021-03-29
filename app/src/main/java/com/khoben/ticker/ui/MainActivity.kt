package com.khoben.ticker.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.khoben.ticker.R
import com.khoben.ticker.common.CheckInternetConnection
import com.khoben.ticker.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val sharedViewModel by viewModel<SharedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAndShowFragment()
        initObservables()
    }

    private fun initObservables() {
        sharedViewModel.searchButtonClicked.observe(this, { clicked ->
            Timber.d("clicked = $clicked")
            if (clicked == true) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_main_container, SearchFragment()).addToBackStack(null)
                    .commit()
            }
        })
        sharedViewModel.firstLoadDatabaseStatus.observe(this, {
            when(it) {
                FirstLoadStatus.START_LOADING -> {
                    binding.loading.visibility = View.VISIBLE
                }
                FirstLoadStatus.LOADED -> {
                    binding.loading.visibility = View.GONE
                }
            }
        })
        CheckInternetConnection.observe(this, {
            when(it) {
                true -> {
                    binding.noInternet.visibility = View.GONE
                }
                false -> {
                    binding.noInternet.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initAndShowFragment() {
        var fragment = supportFragmentManager.findFragmentById(R.id.activity_main_container)
        if (fragment == null) {
            fragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.activity_main_container, fragment)
                .commit()
        }
    }
}