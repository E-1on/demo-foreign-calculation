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
 * Following the precise formula from the requirements
 */
class PayeeTaxCalculationStrategy : TaxCalculationStrategy {
    
    override fun calculateForeignAmounts(
        summary: ForeignRightHolderSummary,
        krwRoundedNrFee: BigDecimal,
        foreignAmountScale: Int
    ): ForeignCalculationResult {
        // 5. 외화 지급총액 foreignTotalPayment = 원화 인접권료 / 환율
        val foreignTotalPayment = krwRoundedNrFee
            .divide(summary.exchangeRate, foreignAmountScale, RoundingMode.HALF_UP)
            
        // 6. 외화 소득세 foreignIncomeTax = 외화 지급총액 * 원천세율 / 1.1
        val foreignIncomeTax = foreignTotalPayment
            .multiply(summary.withholdingTaxRate)
            .divide(BigDecimal("1.1"), foreignAmountScale, RoundingMode.HALF_UP)
            
        // 7. 외화 주민세 foreignResidentTax = 외화 소득세 * 0.1
        val foreignResidentTax = foreignIncomeTax
            .multiply(BigDecimal("0.1"))
            .setScale(foreignAmountScale, RoundingMode.HALF_UP)
            
        // 8. 외화 순지급액 foreignNetPayment = 외화 지급총액 - (외화 소득세 + 외화 주민세)
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
        // 8. 외화 순지급액 foreignNetPayment = 원화 인접권료 / 환율
        val foreignNetPayment = krwRoundedNrFee
            .divide(summary.exchangeRate, foreignAmountScale, RoundingMode.HALF_UP)
        
        // Calculate tax factor (1 - 원천세율)
        val taxFactor = BigDecimal.ONE.subtract(summary.withholdingTaxRate)
        
        // 6. 외화 소득세 foreignIncomeTax = (외화 순지급액 / (1- 원천세율) - 외화 순지급액) / 1.1
        val foreignIncomeTax = foreignNetPayment
            .divide(taxFactor, foreignAmountScale, RoundingMode.HALF_UP)
            .subtract(foreignNetPayment)
            .divide(BigDecimal("1.1"), foreignAmountScale, RoundingMode.HALF_UP)
            
        // 7. 외화 주민세 foreignResidentTax = 외화 소득세 * 0.1
        val foreignResidentTax = foreignIncomeTax
            .multiply(BigDecimal("0.1"))
            .setScale(foreignAmountScale, RoundingMode.HALF_UP)
            
        // 5. 외화 지급총액 foreignTotalPayment = 외화 소득세 + 외화 주민세 + 외화 순지급액
        val foreignTotalPayment = foreignIncomeTax
            .add(foreignResidentTax)
            .add(foreignNetPayment)
            
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
        // 4. 원화 순지급액 krwNetPayment = 외화 순지급액 * 환율
        val krwNetPayment = foreignResult.foreignNetPayment
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        // 2. 원화 소득세 krwIncomeTax = 외화 소득세 * 환율
        val krwIncomeTax = foreignResult.foreignIncomeTax
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        // 3. 원화 주민세 krwResidentTax = 외화 주민세 * 환율
        val krwResidentTax = foreignResult.foreignResidentTax
            .multiply(summary.exchangeRate)
            .setScale(0, RoundingMode.HALF_UP)
            
        // 1. 원화 지급총액 krwTotalPayment = 원화 소득세 + 원화 주민세 + 원화 순지급액
        val krwTotalPayment = krwIncomeTax
            .add(krwResidentTax)
            .add(krwNetPayment)
            
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
