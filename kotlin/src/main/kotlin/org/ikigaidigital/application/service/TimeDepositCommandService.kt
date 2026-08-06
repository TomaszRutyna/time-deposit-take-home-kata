package org.ikigaidigital.application.service

import org.ikigaidigital.domain.deposit.TimeDepositCalculator
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal
import org.ikigaidigital.domain.plan.PlanDefinitionResolver
import org.ikigaidigital.port.`in`.InterestRecalculation
import org.ikigaidigital.port.`in`.UpsertTimeDeposit
import org.ikigaidigital.port.out.TimeDepositRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.math.abs

@Service
class TimeDepositCommandService(
    private val timeDepositRepository: TimeDepositRepository,
    private val planDefinitionResolver: PlanDefinitionResolver,
    private val timeDepositCalculator: TimeDepositCalculator
): UpsertTimeDeposit, InterestRecalculation {

    companion object {
        const val PAGE_SIZE_FOR_RECALCULATION = 50
    }

    @Transactional
    override fun upsertTimeDeposit(timeDeposit: TimeDeposit): TimeDepositWithWithdrawals {
        return if (timeDeposit.id == null) {
            val firstInterestCalculationDate = planDefinitionResolver.resolvePlanDefinition(timeDeposit.planType)
                ?.nextInterestCalculationDate(timeDeposit)

            timeDeposit.nextInterestCalculationDate = firstInterestCalculationDate

            timeDepositRepository.save(timeDeposit)
        } else {
            val withdrawal = timeDepositRepository.getTimeDeposit(timeDeposit.id)
                ?.balance
                ?.takeIf { it != timeDeposit.balance }
                ?.let { Withdrawal(null, abs(it - timeDeposit.balance), LocalDate.now()) }

            timeDepositRepository.save(timeDeposit, withdrawal = withdrawal)
        }
    }

    override fun recalculateInterests() {
        var pageIndex = 0
        var timeDeposits: List<TimeDeposit>

        do {
            timeDeposits = timeDepositRepository.getTimeDepositsForInterestRecalculation(pageIndex++, PAGE_SIZE_FOR_RECALCULATION)

            timeDepositCalculator.updateBalance(timeDeposits)

            timeDeposits.forEach { timeDepositRepository.save(it) }
        } while (timeDeposits.isNotEmpty())
    }
}