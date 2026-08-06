package org.ikigaidigital.adapter.`in`.scheduler

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.ikigaidigital.port.`in`.InterestRecalculation
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InterestRecalculationScheduler(
    private val interestRecalculation: InterestRecalculation
) {
    @SchedulerLock(name = "interestRecalculationLock")
    @Scheduled(cron = $$"${interest-recalculation.cron}")
    fun recalculateInterests() {
        interestRecalculation.recalculateInterests()
    }
}