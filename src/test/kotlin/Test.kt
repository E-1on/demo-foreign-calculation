import com.dreamuscompany.flo.model.ForeignRightHolderSummary
import com.dreamuscompany.flo.MyCalculator
import com.dreamuscompany.flo.model.StatementForeignSettlementCalculation
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class Test {

    @Test
    fun test() {

        val input = input()
        val actualResults = input.map { summary: ForeignRightHolderSummary ->
            //FIXME
            MyCalculator.calculate(summary)
        }.toList()

        actualResults.forEachIndexed { i, result ->
            println("ACTUAL   = ${result}")
            println("EXPECTED = ${expectedOutput()[i]}")
            assertEquals(expectedOutput()[i], result)
        }
    }

    private fun input(): List<ForeignRightHolderSummary> {

        return Test::class.java.getResourceAsStream("/input.tsv")?.bufferedReader()?.useLines { lines ->
            lines.map { line ->
                val values = line.split("\t")
                ForeignRightHolderSummary(
                    settlementYyyymm = values[0],
                    serviceYyyymm = values[1],
                    rightHolderId = values[2].toLong(),
                    rightHolderName = values[3],
                    salesChannel = values[4],
                    neighboringCopyrightFee = BigDecimal(values[5]),
                    hits = values[6].toLong(),
                    companyType = values[7],
                    commonFormatYn = values[8],
                    currency = values[9],
                    exchangeRate = BigDecimal(values[10]),
                    exchangeRateBaseDate = values[11],
                    nation = values[12],
                    withholdingTaxBurdenType = values[13],
                    withholdingTaxRate = BigDecimal(values[14])
                )
            }.toList()
                .sortedBy { it.serviceYyyymm }
                .sortedBy { it.salesChannel }
                .sortedBy { it.rightHolderId }
        }
            ?: throw IllegalStateException("Cannot find resource: /input.tsv")
    }

    private fun expectedOutput(): List<StatementForeignSettlementCalculation> {

        return Test::class.java.getResourceAsStream("/output.tsv")?.bufferedReader()?.useLines { lines ->
            lines.map { line ->
                val values = line.split("\t")
                StatementForeignSettlementCalculation(
                    rightHolderId = values[0].toLong(),
                    rightHolderName = values[1],
                    salesChannel = values[2],
                    neighboringCopyrightFee = BigDecimal(values[3]),
                    hits = values[4].toLong(),
                    companyType = values[5],
                    commonFormatYn = values[6],
                    currency = values[7],
                    exchangeRate = BigDecimal(values[8]),
                    exchangeRateBaseDate = values[9],
                    nation = values[10],
                    withholdingTaxBurdenType = values[11],
                    withholdingTaxRate = BigDecimal(values[12]),
                    foreignTotalPayment = BigDecimal(values[13]),
                    foreignIncomeTax = BigDecimal(values[14]),
                    foreignResidentTax = BigDecimal(values[15]),
                    foreignNetPayment = BigDecimal(values[16]),
                    krwRoundedNeighboringCopyrightFee = BigDecimal(values[17]),
                    currencyTranslationProfit = values[18].toInt(),
                    krwTotalPayment = BigDecimal(values[19]),
                    krwIncomeTax = BigDecimal(values[20]),
                    krwResidentTax = BigDecimal(values[21]),
                    krwNetPayment = BigDecimal(values[22]),
                    serviceYyyymm = values[23],
                    settlementYyyymm = values[24]
                )
            }.toList()
                .sortedBy { it.serviceYyyymm }
                .sortedBy { it.salesChannel }
                .sortedBy { it.rightHolderId }
        }
            ?: throw IllegalStateException("Cannot find resource: /output.tsv")
    }
}
