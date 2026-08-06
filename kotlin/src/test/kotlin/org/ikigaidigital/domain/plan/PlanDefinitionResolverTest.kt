package org.ikigaidigital.domain.plan

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.domain.plan.model.DepositPlanConstraints
import org.ikigaidigital.domain.plan.model.PlanType
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PlanDefinitionResolverTest {

    @Test
    fun shouldGetCorrectPlanDefinition() {
        //given
        val planDefinitionResolver = PlanDefinitionResolver(listOf(
            BasePlanDefinition(PlanType.PREMIUM, DepositPlanConstraints(null, null, BigDecimal.valueOf(0.01), 2)),
            BasePlanDefinition(PlanType.BASIC, DepositPlanConstraints(null, null, BigDecimal.valueOf(0.02), 0))
        ))
        //when
        val definition = planDefinitionResolver.resolvePlanDefinition(PlanType.PREMIUM.value)
        //then
        assertThat(definition?.planType).isEqualTo(PlanType.PREMIUM)
    }
}