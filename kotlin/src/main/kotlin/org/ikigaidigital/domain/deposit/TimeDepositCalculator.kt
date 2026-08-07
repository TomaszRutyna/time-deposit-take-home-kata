package org.ikigaidigital.domain.deposit

import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.plan.PlanDefinitionResolver
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class TimeDepositCalculator(private val planResolver: PlanDefinitionResolver) {

    private val logger = LoggerFactory.getLogger(TimeDepositCalculator::class.java)

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
                val previousBalance = it.balance
                it.balance += interest.toDouble()
                it.forDate = LocalDate.now()
                it.nextInterestCalculationDate = planDefinition?.nextInterestCalculationDate(it, LocalDate.now())
                logger.debug("Deposit id: {} balance updated: {} -> {} (interest: {})",
                    it.id, previousBalance, it.balance, interest)
            } else {
                logger.debug("Deposit id: {} - no interest applied (plan: {})", it.id, it.planType)
            }
        }
    }
}