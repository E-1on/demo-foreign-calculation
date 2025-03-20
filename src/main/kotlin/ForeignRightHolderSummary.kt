package com.dreamuscompany.flo

import java.math.BigDecimal

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
