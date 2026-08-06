package org.ikigaidigital.domain.plan

import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.plan.model.DepositPlanConstraints
import org.ikigaidigital.domain.plan.model.PlanType
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.BigDecimal.valueOf
import java.time.LocalDate
import java.math.RoundingMode.HALF_UP
import java.time.temporal.ChronoUnit

class BasePlanDefinition(
    val planType: PlanType,
    private val constraints: DepositPlanConstraints
) {
    companion object {
        const val NUMBER_OF_MONTH = 12L
    }
    fun calculateInterests(deposit: TimeDeposit): BigDecimal {
        if (constraints.firstInterestCalculationDay != null
            && deposit.days <= constraints.firstInterestCalculationDay) {
            return ZERO
        }

        if (constraints.lastInterestCalculationDay != null
            && deposit.days > constraints.lastInterestCalculationDay) {
            return ZERO
        }

        return valueOf(deposit.balance)
            .multiply(constraints.interestRate)
            .divide(valueOf(NUMBER_OF_MONTH), constraints.numberOfDecimals, HALF_UP)
    }

    fun nextInterestCalculationDate(deposit: TimeDeposit, lastInterestCalculationDate: LocalDate? = null): LocalDate? {
        val now = LocalDate.now()

        val currentDayOfTimeDeposit =
            ChronoUnit.DAYS.between(deposit.forDate?: now, now) + deposit.days

        return when {
            constraints.firstInterestCalculationDay != null
                    && currentDayOfTimeDeposit <= constraints.firstInterestCalculationDay ->
                        now.plusDays(constraints.firstInterestCalculationDay - currentDayOfTimeDeposit + 1)

            constraints.lastInterestCalculationDay != null
                    && currentDayOfTimeDeposit > constraints.lastInterestCalculationDay -> null

            lastInterestCalculationDate == null -> now

            else -> lastInterestCalculationDate.plusMonths(1)
        }
    }
}