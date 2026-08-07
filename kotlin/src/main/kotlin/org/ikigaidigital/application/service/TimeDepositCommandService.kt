package org.ikigaidigital.application.service

import org.ikigaidigital.domain.deposit.TimeDepositCalculator
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal
import org.ikigaidigital.domain.plan.PlanDefinitionResolver
import org.ikigaidigital.port.`in`.InterestRecalculation
import org.ikigaidigital.port.`in`.UpsertTimeDeposit
import org.ikigaidigital.port.out.TimeDepositRepository
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(TimeDepositCommandService::class.java)

    companion object {
        const val PAGE_SIZE_FOR_RECALCULATION = 50
    }

    @Transactional
    override fun upsertTimeDeposit(timeDeposit: TimeDeposit): TimeDepositWithWithdrawals {
        return if (timeDeposit.id == null) {
            logger.info("Creating new time deposit - planType: {}, balance: {}, days: {}",
                timeDeposit.planType, timeDeposit.balance, timeDeposit.days)

            val firstInterestCalculationDate = planDefinitionResolver.resolvePlanDefinition(timeDeposit.planType)
                ?.nextInterestCalculationDate(timeDeposit)

            timeDeposit.nextInterestCalculationDate = firstInterestCalculationDate
            logger.debug("Calculated first interest date: {}", firstInterestCalculationDate)

            timeDepositRepository.save(timeDeposit)
        } else {
            logger.info("Updating time deposit id: {}", timeDeposit.id)

            //if balance changed outside our service it means that withdrawal was made
            val withdrawal = timeDepositRepository.getTimeDeposit(timeDeposit.id)
                ?.balance
                ?.takeIf { it != timeDeposit.balance }
                ?.let {
                    val withdrawalAmount = abs(it - timeDeposit.balance)
                    logger.info("Balance changed for deposit id: {}, recording withdrawal of: {}",
                        timeDeposit.id, withdrawalAmount)
                    Withdrawal(null, withdrawalAmount, LocalDate.now())
                }

            timeDepositRepository.save(timeDeposit, withdrawal = withdrawal)
        }
    }

    override fun recalculateInterests() {
        logger.info("Starting interest recalculation")
        var pageIndex = 0
        var timeDeposits: List<TimeDeposit>
        var totalProcessed = 0

        do {
            timeDeposits = timeDepositRepository.getTimeDepositsForInterestRecalculation(pageIndex++, PAGE_SIZE_FOR_RECALCULATION)

            if (timeDeposits.isNotEmpty()) {
                logger.debug("Processing batch {} with {} deposits", pageIndex, timeDeposits.size)
                timeDepositCalculator.updateBalance(timeDeposits)
                timeDeposits.forEach { timeDepositRepository.save(it) }
                totalProcessed += timeDeposits.size
            }
        } while (timeDeposits.isNotEmpty())

        logger.info("Interest recalculation completed - total deposits processed: {}", totalProcessed)
    }
}