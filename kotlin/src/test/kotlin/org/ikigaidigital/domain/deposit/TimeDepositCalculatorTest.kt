package org.ikigaidigital.domain.deposit

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.plan.BasePlanDefinition
import org.ikigaidigital.domain.plan.PlanDefinitionResolver
import org.ikigaidigital.domain.plan.model.DepositPlanConstraints
import org.ikigaidigital.domain.plan.model.PlanType
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class TimeDepositCalculatorTest {
    @Test
    fun shouldUpdateBalanceWhenDefinitionExists() {
        //given
        val calc = TimeDepositCalculator(
            PlanDefinitionResolver(listOf(
                BasePlanDefinition(PlanType.BASIC,
                    DepositPlanConstraints(30, 366, BigDecimal.valueOf(0.01), 2))
            ))
        )
        val plans = listOf(
            TimeDeposit(1, "basic", 1234567.00, 45)
        )
        //when
        calc.updateBalance(plans)
        //then
        assertThat(plans[0].balance).isEqualTo(1235595.81)
        assertThat(plans[0].forDate).isEqualTo(LocalDate.now())
    }

    @Test
    fun shouldNotUpdateBalanceWhenPlanDoesNotExist() {
        //given
        val calc = TimeDepositCalculator(
            PlanDefinitionResolver(listOf(
                BasePlanDefinition(PlanType.BASIC,
                    DepositPlanConstraints(30, 366, BigDecimal.valueOf(0.01), 2))
            ))
        )
        val plans = listOf(
            TimeDeposit(1, "standart", 1234567.00, 45)
        )
        //when
        calc.updateBalance(plans)
        //then
        assertThat(plans[0].balance).isEqualTo(1234567.00)
        assertThat(plans[0].forDate).isEqualTo(LocalDate.now())
    }
}