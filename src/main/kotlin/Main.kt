package com.dreamuscompany.flo

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Input
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

interface Calculator {
    fun calculate(summary: ForeignRightHolderSummary): StatementForeignSettlementCalculation
}

object MyCalculator : Calculator {

    override fun calculate(summary: ForeignRightHolderSummary): StatementForeignSettlementCalculation {

        if (summary.withholdingTaxBurdenType == "PAYEE") {

            val foreignAmountScaleByCurrency = if (summary.currency == "JPY") 0 else 2
            val krwRoundedNrFee = summary.neighboringCopyrightFee.setScale(0, RoundingMode.HALF_UP)
            val foreignTotalPayment =
                krwRoundedNrFee.divide(summary.exchangeRate, foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignIncomeTax = foreignTotalPayment.multiply(summary.withholdingTaxRate)
                .divide(BigDecimal(1.1), foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignResidentTax = foreignIncomeTax.multiply(BigDecimal(0.1))
                .setScale(foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignNetPayment = foreignTotalPayment.subtract(foreignIncomeTax).subtract(foreignResidentTax)
            val krwTotalPayment = foreignTotalPayment.multiply(summary.exchangeRate).setScale(0, RoundingMode.HALF_UP)
            val krwIncomeTax = foreignIncomeTax.multiply(summary.exchangeRate).setScale(0, RoundingMode.HALF_UP)
            val krwResidentTax = foreignResidentTax.multiply(summary.exchangeRate).setScale(0, RoundingMode.HALF_UP)
            val krwNetPayment = krwTotalPayment.subtract(krwIncomeTax).subtract(krwResidentTax)
            val currencyTranslationProfit = krwTotalPayment.subtract(krwRoundedNrFee).toInt()

            return StatementForeignSettlementCalculation(
                summary.rightHolderId,
                summary.rightHolderName,
                summary.salesChannel,
                summary.neighboringCopyrightFee,
                summary.hits,
                summary.commonFormatYn,
                summary.companyType,
                summary.currency,
                summary.exchangeRate,
                summary.exchangeRateBaseDate,
                summary.nation,
                summary.withholdingTaxBurdenType,
                summary.withholdingTaxRate,
                krwRoundedNrFee.divide(summary.exchangeRate, foreignAmountScaleByCurrency, RoundingMode.HALF_UP),
                foreignIncomeTax,
                foreignResidentTax,
                foreignNetPayment,
                currencyTranslationProfit,
                krwRoundedNrFee,
                krwTotalPayment,
                krwIncomeTax,
                krwResidentTax,
                krwNetPayment,
                summary.serviceYyyymm,
                summary.settlementYyyymm
            )
        }


        if (summary.withholdingTaxBurdenType == "PAYER") {

            val foreignAmountScaleByCurrency = if (summary.currency == "JPY") 0 else 2
            val krwRoundedNrFee = summary.neighboringCopyrightFee.setScale(0, RoundingMode.HALF_UP)
            val foreignNetPayment =
                krwRoundedNrFee.divide(summary.exchangeRate, foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignWithHoldingTax: BigDecimal =
                foreignNetPayment.divide(BigDecimal(0.85), foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
                    .subtract(foreignNetPayment)
            val foreignIncomeTax = foreignWithHoldingTax.divide(
                BigDecimal(1.1),
                foreignAmountScaleByCurrency,
                RoundingMode.HALF_UP
            )
            val foreignResidentTax = foreignIncomeTax.multiply(BigDecimal(0.1))
                .setScale(foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignTotalPayment = foreignNetPayment.add(foreignIncomeTax).add(foreignResidentTax)
            val krwNetPayment = foreignNetPayment.multiply(summary.exchangeRate)
                .setScale(0, RoundingMode.HALF_UP)
            val krwIncomeTax = foreignIncomeTax.multiply(summary.exchangeRate)
                .setScale(0, RoundingMode.HALF_UP)
            val krwResidentTax = foreignResidentTax.multiply(summary.exchangeRate)
                .setScale(0, RoundingMode.HALF_UP)
            val krwTotalPayment = krwNetPayment.add(krwIncomeTax).add(krwResidentTax)
            val currencyTranslationProfit = krwNetPayment.subtract(krwRoundedNrFee).toInt()

            return StatementForeignSettlementCalculation(
                summary.rightHolderId,
                summary.rightHolderName,
                summary.salesChannel,
                summary.neighboringCopyrightFee,
                summary.hits,
                summary.commonFormatYn,
                summary.companyType,
                summary.currency,
                summary.exchangeRate,
                summary.exchangeRateBaseDate,
                summary.nation,
                summary.withholdingTaxBurdenType,
                summary.withholdingTaxRate,
                foreignTotalPayment,
                foreignIncomeTax,
                foreignResidentTax,
                foreignNetPayment,
                currencyTranslationProfit,
                krwRoundedNrFee,
                krwTotalPayment,
                krwIncomeTax,
                krwResidentTax,
                krwNetPayment,
                summary.serviceYyyymm,
                summary.settlementYyyymm
            )
        }

        throw IllegalArgumentException()
    }
}
