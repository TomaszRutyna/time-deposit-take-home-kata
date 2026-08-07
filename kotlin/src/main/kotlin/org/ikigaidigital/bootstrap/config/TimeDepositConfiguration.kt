package org.ikigaidigital.bootstrap.config

import org.ikigaidigital.domain.deposit.TimeDepositCalculator
import org.ikigaidigital.domain.plan.BasePlanDefinition
import org.ikigaidigital.domain.plan.PlanDefinitionResolver
import org.ikigaidigital.domain.plan.model.DepositPlanConstraints
import org.ikigaidigital.domain.plan.model.PlanType
import org.ikigaidigital.domain.plan.model.PlanType.BASIC
import org.ikigaidigital.domain.plan.model.PlanType.PREMIUM
import org.ikigaidigital.domain.plan.model.PlanType.STUDENT
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal

@Configuration
@EnableConfigurationProperties(TimeDepositPlans::class)
class TimeDepositConfiguration {

    @Bean
    fun planDefinitionResolver(timeDepositPlans: TimeDepositPlans): PlanDefinitionResolver {
        return PlanDefinitionResolver(listOf(
            preparePlanDefinition(PREMIUM, timeDepositPlans),
            preparePlanDefinition(BASIC, timeDepositPlans),
            preparePlanDefinition(STUDENT, timeDepositPlans)
        ))
    }

    @Bean
    fun timeDepositCalculator(planDefinitionResolver: PlanDefinitionResolver) = TimeDepositCalculator(planDefinitionResolver)

    private fun preparePlanDefinition(type: PlanType, timeDepositPlans: TimeDepositPlans): BasePlanDefinition {
        val constraints = timeDepositPlans.plans[type.value]
            ?.toPlanDefinitionConstraints()
            ?: throw IllegalStateException("Missing definition for plan $type")

        return BasePlanDefinition(type, constraints)
    }
}

@ConfigurationProperties(prefix = "time-deposit")
data class TimeDepositPlans(
    val plans: Map<String, TimeDepositPlanDefinition>
)

data class TimeDepositPlanDefinition(
    val firstInterestCalculationDay: Int? = null,
    val lastInterestCalculationDay: Int? = null,
    val interestRate: BigDecimal,
    val decimalPlaces: Int
) {
    fun toPlanDefinitionConstraints(): DepositPlanConstraints {
        return DepositPlanConstraints(
            firstInterestCalculationDay, lastInterestCalculationDay, interestRate, decimalPlaces
        )
    }
}