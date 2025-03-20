package com.dreamuscompany.flo.calculator

import com.dreamuscompany.flo.calculator.factories.TaxCalculationStrategyFactory
import com.dreamuscompany.flo.calculator.providers.DefaultCurrencyScaleProvider
import com.dreamuscompany.flo.model.ForeignRightHolderSummary
import com.dreamuscompany.flo.model.StatementForeignSettlementCalculation
import java.math.RoundingMode

/**
 * Main implementation of the ForeignSettlementCalculator
 * Following Dependency Inversion Principle by depending on abstractions
 */
class ForeignSettlementCalculatorImpl(
    private val currencyScaleProvider: CurrencyScaleProvider = DefaultCurrencyScaleProvider()
) : ForeignSettlementCalculator {

    override fun calculate(summary: ForeignRightHolderSummary): StatementForeignSettlementCalculation {
        // Get the appropriate strategy based on the tax burden type
        val taxStrategy = TaxCalculationStrategyFactory.createStrategy(summary.withholdingTaxBurdenType)
        
        // Determine the currency scale
        val foreignAmountScale = currencyScaleProvider.getForeignAmountScale(summary.currency)
        
        // Round neighboring copyright fee
        val krwRoundedNrFee = summary.neighboringCopyrightFee.setScale(0, RoundingMode.HALF_UP)
        
        // Calculate foreign amounts
        val foreignResult = taxStrategy.calculateForeignAmounts(
            summary, 
            krwRoundedNrFee, 
            foreignAmountScale
        )
        
        // Calculate KRW amounts
        val krwResult = taxStrategy.calculateKrwAmounts(
            summary,
            foreignResult,
            krwRoundedNrFee
        )
        
        // Create and return the output model
        return StatementForeignSettlementCalculation(
            rightHolderId = summary.rightHolderId,
            rightHolderName = summary.rightHolderName,
            salesChannel = summary.salesChannel,
            neighboringCopyrightFee = summary.neighboringCopyrightFee,
            hits = summary.hits,
            commonFormatYn = summary.commonFormatYn,
            companyType = summary.companyType,
            currency = summary.currency,
            exchangeRate = summary.exchangeRate,
            exchangeRateBaseDate = summary.exchangeRateBaseDate,
            nation = summary.nation,
            withholdingTaxBurdenType = summary.withholdingTaxBurdenType,
            withholdingTaxRate = summary.withholdingTaxRate,
            foreignTotalPayment = foreignResult.foreignTotalPayment,
            foreignIncomeTax = foreignResult.foreignIncomeTax,
            foreignResidentTax = foreignResult.foreignResidentTax,
            foreignNetPayment = foreignResult.foreignNetPayment,
            currencyTranslationProfit = krwResult.currencyTranslationProfit,
            krwRoundedNeighboringCopyrightFee = krwRoundedNrFee,
            krwTotalPayment = krwResult.krwTotalPayment,
            krwIncomeTax = krwResult.krwIncomeTax,
            krwResidentTax = krwResult.krwResidentTax,
            krwNetPayment = krwResult.krwNetPayment,
            serviceYyyymm = summary.serviceYyyymm,
            settlementYyyymm = summary.settlementYyyymm
        )
    }
}
