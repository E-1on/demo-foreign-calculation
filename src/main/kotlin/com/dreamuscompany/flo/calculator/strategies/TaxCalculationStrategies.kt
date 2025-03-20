package com.dreamuscompany.flo.calculator.strategies

import com.dreamuscompany.flo.calculator.ForeignCalculationResult
import com.dreamuscompany.flo.calculator.KrwCalculationResult
import com.dreamuscompany.flo.calculator.TaxCalculationStrategy
import com.dreamuscompany.flo.model.ForeignRightHolderSummary
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * PAYEE (수취자부담) tax calculation strategy
 * Implements the Strategy pattern for the PAYEE tax calculation
 */
class PayeeTaxCalculationStrategy : TaxCalculationStrategy {
    
    override fun calculateForeignAmounts(
        summary: ForeignRightHolderSummary,
        krwRoundedNrFee: BigDecimal,
        foreignAmountScale: Int
    ): ForeignCalculationResult {
        val foreignTotalPayment = krwRoundedNrFee
            .divide(summary.exchangeRate, foreignAmountScale, RoundingMode.HALF_UP)
            
        val foreignIncomeTax = foreignTotalPayment
            .multiply(summary.withholdingTaxRate)
            .divide(BigDecimal(1.1), foreignAmountScale, RoundingMode.HALF_UP)
            
        val foreignResidentTax = foreignIncomeTax
            .multiply(BigDecimal(0.1))
            .setScale(foreignAmountScale, RoundingMode.HALF_UP)
            
        val foreignNetPayment = foreignTotalPayment
            .subtract(foreignIncomeTax)
            .subtract(foreignResidentTax)
            
        return ForeignCalculationResult(
            foreignTotalPayment = foreignTotalPayment,
            foreignIncomeTax = foreignIncomeTax,
            foreignResidentTax = foreignResidentTax,
            foreignNetPayment = foreignNetPayment
        )
    }
    
    override fun calculateKrwAmounts(
        summary: ForeignRightHolderSummary,
        foreignResult: ForeignCalculationResult,
        krwRoundedNrFee: BigDecimal
    ): KrwCalculationResult {
        val krwTotalPayment = foreignResult.foreignTotalPayment
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        val krwIncomeTax = foreignResult.foreignIncomeTax
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        val krwResidentTax = foreignResult.foreignResidentTax
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        val krwNetPayment = krwTotalPayment
            .subtract(krwIncomeTax)
            .subtract(krwResidentTax)
            
        val currencyTranslationProfit = krwTotalPayment
            .subtract(krwRoundedNrFee)
            .toInt()
            
        return KrwCalculationResult(
            krwTotalPayment = krwTotalPayment,
            krwIncomeTax = krwIncomeTax,
            krwResidentTax = krwResidentTax,
            krwNetPayment = krwNetPayment,
            currencyTranslationProfit = currencyTranslationProfit
        )
    }
}

/**
 * PAYER (납부자부담) tax calculation strategy
 * Implements the Strategy pattern for the PAYER tax calculation
 */
class PayerTaxCalculationStrategy : TaxCalculationStrategy {
    
    override fun calculateForeignAmounts(
        summary: ForeignRightHolderSummary,
        krwRoundedNrFee: BigDecimal,
        foreignAmountScale: Int
    ): ForeignCalculationResult {
        val foreignNetPayment = krwRoundedNrFee
            .divide(summary.exchangeRate, foreignAmountScale, RoundingMode.HALF_UP)
            
        val foreignWithHoldingTax = foreignNetPayment
            .divide(BigDecimal(0.85), foreignAmountScale, RoundingMode.HALF_UP)
            .subtract(foreignNetPayment)
            
        val foreignIncomeTax = foreignWithHoldingTax
            .divide(BigDecimal(1.1), foreignAmountScale, RoundingMode.HALF_UP)
            
        val foreignResidentTax = foreignIncomeTax
            .multiply(BigDecimal(0.1))
            .setScale(foreignAmountScale, RoundingMode.HALF_UP)
            
        val foreignTotalPayment = foreignNetPayment
            .add(foreignIncomeTax)
            .add(foreignResidentTax)
            
        return ForeignCalculationResult(
            foreignTotalPayment = foreignTotalPayment,
            foreignIncomeTax = foreignIncomeTax,
            foreignResidentTax = foreignResidentTax,
            foreignNetPayment = foreignNetPayment
        )
    }
    
    override fun calculateKrwAmounts(
        summary: ForeignRightHolderSummary,
        foreignResult: ForeignCalculationResult,
        krwRoundedNrFee: BigDecimal
    ): KrwCalculationResult {
        val krwNetPayment = foreignResult.foreignNetPayment
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        val krwIncomeTax = foreignResult.foreignIncomeTax
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        val krwResidentTax = foreignResult.foreignResidentTax
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        val krwTotalPayment = krwNetPayment
            .add(krwIncomeTax)
            .add(krwResidentTax)
            
        val currencyTranslationProfit = krwNetPayment
            .subtract(krwRoundedNrFee)
            .toInt()
            
        return KrwCalculationResult(
            krwTotalPayment = krwTotalPayment,
            krwIncomeTax = krwIncomeTax,
            krwResidentTax = krwResidentTax,
            krwNetPayment = krwNetPayment,
            currencyTranslationProfit = currencyTranslationProfit
        )
    }
}
