package com.dreamuscompany.flo.calculator.factories

import com.dreamuscompany.flo.calculator.TaxCalculationStrategy
import com.dreamuscompany.flo.calculator.strategies.PayeeTaxCalculationStrategy
import com.dreamuscompany.flo.calculator.strategies.PayerTaxCalculationStrategy

/**
 * Factory for creating appropriate tax calculation strategy
 * Following Open/Closed Principle by making it easy to add new strategies
 */
class TaxCalculationStrategyFactory {
    
    companion object {
        private const val PAYEE = "PAYEE"
        private const val PAYER = "PAYER"
        
        fun createStrategy(withholdingTaxBurdenType: String): TaxCalculationStrategy {
            return when (withholdingTaxBurdenType) {
                PAYEE -> PayeeTaxCalculationStrategy()
                PAYER -> PayerTaxCalculationStrategy()
                else -> throw IllegalArgumentException("Unknown withholding tax burden type: $withholdingTaxBurdenType")
            }
        }
    }
}
