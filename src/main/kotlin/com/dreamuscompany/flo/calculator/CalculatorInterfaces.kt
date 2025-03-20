package com.dreamuscompany.flo.calculator

import com.dreamuscompany.flo.model.ForeignRightHolderSummary
import com.dreamuscompany.flo.model.StatementForeignSettlementCalculation
import java.math.BigDecimal

/**
 * Main calculator interface following Interface Segregation Principle
 */
interface ForeignSettlementCalculator {
    fun calculate(summary: ForeignRightHolderSummary): StatementForeignSettlementCalculation
}

/**
 * Interface for currency scale determination
 */
interface CurrencyScaleProvider {
    fun getForeignAmountScale(currency: String): Int
}

/**
 * Interface for tax calculation strategies
 */
interface TaxCalculationStrategy {
    fun calculateForeignAmounts(
        summary: ForeignRightHolderSummary, 
        krwRoundedNrFee: BigDecimal,
        foreignAmountScale: Int
    ): ForeignCalculationResult
    
    fun calculateKrwAmounts(
        summary: ForeignRightHolderSummary,
        foreignResult: ForeignCalculationResult,
        krwRoundedNrFee: BigDecimal
    ): KrwCalculationResult
}

/**
 * Data class to hold foreign currency calculation results
 */
data class ForeignCalculationResult(
    val foreignTotalPayment: BigDecimal,
    val foreignIncomeTax: BigDecimal,
    val foreignResidentTax: BigDecimal,
    val foreignNetPayment: BigDecimal
)

/**
 * Data class to hold KRW calculation results
 */
data class KrwCalculationResult(
    val krwTotalPayment: BigDecimal,
    val krwIncomeTax: BigDecimal,
    val krwResidentTax: BigDecimal,
    val krwNetPayment: BigDecimal,
    val currencyTranslationProfit: Int
)
