package org.ikigaidigital.domain.plan

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.plan.model.DepositPlanConstraints
import org.ikigaidigital.domain.plan.model.PlanType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.LocalDate
import java.util.stream.Stream

class BasePlanDefinitionTest {

    @ParameterizedTest
    @MethodSource("interestCalculationData")
    fun shouldCalculateInterest(
        firstInterestCalculationDate: Int?,
        lastInterestCalculationDate: Int?,
        dayOfDeposit: Int,
        expectedInterestAmount: BigDecimal
    ) {
        //given
        val deposit = TimeDeposit(1, PlanType.STUDENT.value, 1000.00, dayOfDeposit)

        val definition = BasePlanDefinition(PlanType.STUDENT,
            DepositPlanConstraints(
                firstInterestCalculationDate,
                    lastInterestCalculationDate,
                BigDecimal.valueOf(0.01),
                2
            )
        )
        //when
        val calculatedInterest = definition.calculateInterests(deposit)
        //then
        assertThat(calculatedInterest).isEqualTo(expectedInterestAmount)
    }

    @ParameterizedTest
    @MethodSource("interestDateCalculationData")
    fun shouldCalculateNextInterestCalculationDate(
        firstInterestCalculationDay: Int?,
        lastInterestCalculationDay: Int?,
        dayOfDeposit: Int,
        lastInterestRecalculationDate: LocalDate?,
        expectedDateOfNextIInterestCalculation: LocalDate?
    ) {
        //given
        val deposit = TimeDeposit(1, PlanType.STUDENT.value, 1000.00, dayOfDeposit)

        val definition = BasePlanDefinition(PlanType.STUDENT,
            DepositPlanConstraints(
                firstInterestCalculationDay,
                lastInterestCalculationDay,
                BigDecimal.valueOf(0.01),
                2
            )
        )
        //when
        val nextInterestCalculationDate = definition.nextInterestCalculationDate(deposit, lastInterestRecalculationDate)
        //then
        assertThat(nextInterestCalculationDate).isEqualTo(expectedDateOfNextIInterestCalculation)
    }

    companion object {
        @JvmStatic
        @SuppressWarnings("UnusedPrivateMember")
        private fun interestCalculationData(): Stream<Arguments> {
            return Stream.of(
                of(30, 366, 45, BigDecimal.valueOf(0.83)),
                of(30, null, 15, BigDecimal.ZERO),
                of(30, 366, 415, BigDecimal.ZERO),
                of(null, null, 415, BigDecimal.valueOf(0.83)),
            )
        }

        @JvmStatic
        @SuppressWarnings("UnusedPrivateMember")
        private fun interestDateCalculationData(): Stream<Arguments> {
            return Stream.of(
                of(30, 366, 45, null, LocalDate.now()),
                of(30, 366, 15, null, LocalDate.now().plusDays(16)),
                of(30, 366, 415, null, null),
                of(30, 366, 230, LocalDate.now(), LocalDate.now().plusMonths(1)),
            )
        }
    }
}