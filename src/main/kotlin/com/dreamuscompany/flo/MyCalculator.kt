package com.dreamuscompany.flo

import com.dreamuscompany.flo.calculator.ForeignSettlementCalculator
import com.dreamuscompany.flo.calculator.ForeignSettlementCalculatorImpl
import com.dreamuscompany.flo.model.ForeignRightHolderSummary
import com.dreamuscompany.flo.model.StatementForeignSettlementCalculation

/**
 * Entry point for the calculator that maintains backward compatibility
 * with existing tests but delegates to the new SOLID implementation
 */
object MyCalculator {
    private val calculator: ForeignSettlementCalculator = ForeignSettlementCalculatorImpl()
    
    fun calculate(summary: ForeignRightHolderSummary): StatementForeignSettlementCalculation {
        return calculator.calculate(summary)
    }
}
