package com.dreamuscompany.flo.calculator.providers

import com.dreamuscompany.flo.calculator.CurrencyScaleProvider

/**
 * Default implementation of CurrencyScaleProvider
 * Following Single Responsibility Principle by isolating currency scale determination logic
 */
class DefaultCurrencyScaleProvider : CurrencyScaleProvider {
    override fun getForeignAmountScale(currency: String): Int {
        return when (currency) {
            "JPY" -> 0
            else -> 2
        }
    }
}
