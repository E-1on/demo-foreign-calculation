package com.dreamuscompany.flo.model

import java.math.BigDecimal

/**
 * Input model for foreign right holder settlement calculation
 */
data class ForeignRightHolderSummary(
    // 정산월
    val settlementYyyymm: String,
    // 판매월
    val serviceYyyymm: String,
    // 권리사ID
    val rightHolderId: Long,
    // 권리사명
    val rightHolderName: String,
    // 판매채널 FLO/VCOLROING
    val salesChannel: String,
    // 총 발생한 인접권료 (원화 기준)
    val neighboringCopyrightFee: BigDecimal,
    // 총 히트수
    val hits: Long,
    val companyType: String,
    val commonFormatYn: String,
    // 통화
    val currency: String,
    // 환율 ex) 1315.20
    val exchangeRate: BigDecimal,
    val exchangeRateBaseDate: String,
    val nation: String,
    // 원천세부담유형 PAYEE(수취자부담), PAYER(납부자부담) 중 하나, 이 유형에 따라 금액 계산식이 달라진다.
    val withholdingTaxBurdenType: String,
    // 원천세율 ex) 0.11
    val withholdingTaxRate: BigDecimal,
)

/**
 * Output model for foreign right holder settlement calculation
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
