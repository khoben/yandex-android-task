package com.khoben.ticker.common

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