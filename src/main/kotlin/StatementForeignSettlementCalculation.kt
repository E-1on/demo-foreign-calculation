package com.dreamuscompany.flo

import java.math.BigDecimal

/**
 * Output
 */
data class StatementForeignSettlementCalculation(
    val rightHolderId: Long,
    val rightHolderName: String,
    val salesChannel: String,
    val neighboringCopyrightFee: BigDecimal,
    val hits: Long,
    val commonFormatYn: String,
    val companyType: String,
    val currency: String,
    val exchangeRate: BigDecimal,
    val exchangeRateBaseDate: String,
    val nation: String,
    val withholdingTaxBurdenType: String,
    val withholdingTaxRate: BigDecimal,
    // 외화 총지급액
    val foreignTotalPayment: BigDecimal,
    // 외화 소득세
    val foreignIncomeTax: BigDecimal,
    // 외화 주민세
    val foreignResidentTax: BigDecimal,
    // 외화 순지급액
    val foreignNetPayment: BigDecimal,
    // 통화환산이익
    val currencyTranslationProfit: Int,
    // 원화 반올림 인접권료
    val krwRoundedNeighboringCopyrightFee: BigDecimal,
    // 원화 총지급액
    val krwTotalPayment: BigDecimal,
    // 원화 소득세
    val krwIncomeTax: BigDecimal,
    // 원화 주민세
    val krwResidentTax: BigDecimal,
    // 원화 순지급액
    val krwNetPayment: BigDecimal,
    val serviceYyyymm: String,
    val settlementYyyymm: String,
)