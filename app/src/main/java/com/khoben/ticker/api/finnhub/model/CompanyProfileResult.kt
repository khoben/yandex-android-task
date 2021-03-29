package com.khoben.ticker.api.finnhub.model

import kotlinx.serialization.Serializable

@Serializable
data class CompanyProfileResult(
    var country: String? = null,
    val currency: String? = "USD",
    val exchange: String? = null,
    val finnhubIndustry: String? = null,
    val ipo: String? = null,
    val logo: String? = null,
    val marketCapitalization: Double? = null,
    val name: String,
    val phone: String? = null,
    val shareOutstanding: Double? = null,
    val ticker: String,
    val weburl: String? = null
)
