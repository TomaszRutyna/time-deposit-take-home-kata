package org.ikigaidigital.adapter.`in`.rest.mapper

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.adapter.rest.model.TimeDepositRequest
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal
import org.ikigaidigital.domain.deposit.model.Withdrawals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class TimeDepositRestMapperTest {

    @Test
    fun shouldMapRequestToDomain() {
        //given
        val request = TimeDepositRequest(
            id = 1,
            amount = BigDecimal.valueOf(5000.50),
            days = 90,
            planType = "basic"
        )
        //when
        val domain = request.toDomain()
        //then
        assertThat(domain.id).isEqualTo(1)
        assertThat(domain.planType).isEqualTo("basic")
        assertThat(domain.balance).isEqualTo(5000.50)
        assertThat(domain.days).isEqualTo(90)
        assertThat(domain.forDate).isEqualTo(LocalDate.now())
    }

    @Test
    fun shouldMapRequestWithoutIdToDomain() {
        //given
        val request = TimeDepositRequest(
            amount = BigDecimal.valueOf(1000.00),
            days = 30,
            planType = "student"
        )
        //when
        val domain = request.toDomain()
        //then
        assertThat(domain.id).isNull()
        assertThat(domain.planType).isEqualTo("student")
        assertThat(domain.balance).isEqualTo(1000.00)
        assertThat(domain.days).isEqualTo(30)
    }

    @Test
    fun shouldMapDomainWithWithdrawalsToResponse() {
        //given
        val withdrawalDate = LocalDate.of(2025, 6, 15)
        val deposit = TimeDeposit(
            id = 5,
            planType = "basic",
            balance = 9500.75,
            days = 180
        )
        val withdrawals = Withdrawals(
            timeDeposit = 5,
            withdrawals = listOf(
                Withdrawal(id = 10, amount = 200.50, date = withdrawalDate),
                Withdrawal(id = 11, amount = 300.25, date = withdrawalDate.plusDays(10))
            )
        )
        val domainWithWithdrawals = TimeDepositWithWithdrawals(deposit, withdrawals)
        //when
        val response = domainWithWithdrawals.toResponse()
        //then
        assertThat(response.id).isEqualTo(5)
        assertThat(response.amount).isEqualByComparingTo(BigDecimal.valueOf(9500.75))
        assertThat(response.days).isEqualTo(180)
        assertThat(response.planType).isEqualTo("basic")
        val responseWithdrawals = response.withdrawals!!
        assertThat(responseWithdrawals).hasSize(2)
        assertThat(responseWithdrawals[0].id).isEqualTo(10)
        assertThat(responseWithdrawals[0].amount).isEqualByComparingTo(BigDecimal.valueOf(200.50))
        assertThat(responseWithdrawals[0].date).isEqualTo(withdrawalDate)
        assertThat(responseWithdrawals[1].id).isEqualTo(11)
        assertThat(responseWithdrawals[1].amount).isEqualByComparingTo(BigDecimal.valueOf(300.25))
        assertThat(responseWithdrawals[1].date).isEqualTo(withdrawalDate.plusDays(10))
    }

    @Test
    fun shouldMapDomainWithEmptyWithdrawalsToResponse() {
        //given
        val deposit = TimeDeposit(
            id = 3,
            planType = "student",
            balance = 2000.00,
            days = 60
        )
        val withdrawals = Withdrawals(timeDeposit = 3, withdrawals = emptyList())
        val domainWithWithdrawals = TimeDepositWithWithdrawals(deposit, withdrawals)
        //when
        val response = domainWithWithdrawals.toResponse()
        //then
        assertThat(response.id).isEqualTo(3)
        assertThat(response.amount).isEqualByComparingTo(BigDecimal.valueOf(2000.00))
        assertThat(response.days).isEqualTo(60)
        assertThat(response.planType).isEqualTo("student")
        assertThat(response.withdrawals).isEmpty()
    }
}
