package org.ikigaidigital.adapter.`in`.scheduler

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.ikigaidigital.port.`in`.InterestRecalculation
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InterestRecalculationScheduler(
    private val interestRecalculation: InterestRecalculation
) {
    private val logger = LoggerFactory.getLogger(InterestRecalculationScheduler::class.java)

    @SchedulerLock(name = "interestRecalculationLock")
    @Scheduled(cron = $$"${interest-recalculation.cron}")
    fun recalculateInterests() {
        logger.info("Scheduled interest recalculation triggered")
        try {
            interestRecalculation.recalculateInterests()
            logger.info("Scheduled interest recalculation finished successfully")
        } catch (e: Exception) {
            logger.error("Scheduled interest recalculation failed", e)
            throw e
        }
    }
}