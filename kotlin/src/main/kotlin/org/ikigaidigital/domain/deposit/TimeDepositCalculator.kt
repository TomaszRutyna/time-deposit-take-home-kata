package org.ikigaidigital.domain.deposit

import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.plan.PlanDefinitionResolver
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class TimeDepositCalculator(private val planResolver: PlanDefinitionResolver) {

    companion object {
        const val DEFAULT_DECIMAL_PLACES = 2
    }

    //method is breaking immutability, but due to task requirement it cannot be changed
    fun updateBalance(timeDeposits: List<TimeDeposit>) {
        timeDeposits.forEach {
            val planDefinition = planResolver.resolvePlanDefinition(it.planType)
            val interest = planDefinition
                ?.calculateInterests(it)
                ?: BigDecimal.ZERO.setScale(DEFAULT_DECIMAL_PLACES, RoundingMode.HALF_UP)

            if (interest.signum() != 0) {
                it.balance += interest.toDouble()
                it.forDate = LocalDate.now()
                it.nextInterestCalculationDate = planDefinition?.nextInterestCalculationDate(it, LocalDate.now())
            }
        }
    }
}