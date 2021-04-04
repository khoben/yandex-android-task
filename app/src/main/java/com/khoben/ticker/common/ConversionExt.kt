package com.khoben.ticker.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun String?.toCurrencySign(): String {
    return when (this) {
        "USD" -> "$"
        "GBP" -> "£"
        "EUR" -> "€"
        "RUB" -> "₽"
        null -> ""
        else -> this
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.formatDate(format: String): String {
    return SimpleDateFormat(format).format(Date(this))
}

@SuppressLint("SimpleDateFormat")
fun Pair<Int, Double>.toDataPrice(currency: String?): String {
    return "${currency.toCurrencySign()}$second\n${
        (first * 1000L).formatDate("MMMM d, yyyy HH:mm")
    }"
}
