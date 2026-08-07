package org.ikigaidigital.adapter.out.persistence.mapper

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.Withdrawal
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class TimeDepositEntityMapperTest {

    private val today = LocalDate.now()

    @Test
    fun shouldMapDomainTimeDepositToEntity() {
        //given
        val domain = TimeDeposit(
            id = null,
            planType = "basic",
            balance = 5000.50,
            days = 90,
            forDate = today,
            nextInterestCalculationDate = today.plusDays(30)
        )
        //when
        val entity = domain.toEntity()
        //then
        assertThat(entity.id).isNull()
        assertThat(entity.planType).isEqualTo("basic")
        assertThat(entity.balance).isEqualByComparingTo(BigDecimal.valueOf(5000.50))
        assertThat(entity.forDate).isEqualTo(today)
        assertThat(entity.dayOfDeposit).isEqualTo(90)
        assertThat(entity.nextInterestCalculationDate).isEqualTo(today.plusDays(30))
        assertThat(entity.version).isEqualTo(0)
    }

    @Test
    fun shouldMapDomainTimeDepositWithNullForDateToEntityUsingNow() {
        //given
        val domain = TimeDeposit(
            id = null,
            planType = "student",
            balance = 1000.00,
            days = 30,
            forDate = null
        )
        //when
        val entity = domain.toEntity()
        //then
        assertThat(entity.forDate).isEqualTo(LocalDate.now())
    }

    @Test
    fun shouldMapDomainWithdrawalToEntity() {
        //given
        val parentEntity = TimeDepositEntity(
            id = 1,
            planType = "basic",
            balance = BigDecimal.valueOf(5000.00),
            forDate = today,
            dayOfDeposit = 90,
            version = 1
        )
        val withdrawal = Withdrawal(id = 10, amount = 200.50, date = today)
        //when
        val entity = withdrawal.toEntity(parentEntity)
        //then
        assertThat(entity.id).isNull()
        assertThat(entity.amount).isEqualByComparingTo(BigDecimal.valueOf(200.50))
        assertThat(entity.date).isEqualTo(today)
        assertThat(entity.timeDeposit).isEqualTo(parentEntity)
    }

    @Test
    fun shouldMapEntityToDomain() {
        //given
        val entity = TimeDepositEntity(
            id = 7,
            planType = "basic",
            balance = BigDecimal.valueOf(9500.75),
            forDate = today,
            dayOfDeposit = 180,
            nextInterestCalculationDate = today.plusDays(30),
            version = 2
        )
        //when
        val domain = entity.toDomain()
        //then
        assertThat(domain.id).isEqualTo(7)
        assertThat(domain.planType).isEqualTo("basic")
        assertThat(domain.balance).isEqualTo(9500.75)
        assertThat(domain.days).isEqualTo(180)
        assertThat(domain.forDate).isEqualTo(today)
        assertThat(domain.nextInterestCalculationDate).isEqualTo(today.plusDays(30))
    }

    @Test
    fun shouldMapEntityToDomainWithWithdrawals() {
        //given
        val depositEntity = TimeDepositEntity(
            id = 5,
            planType = "student",
            balance = BigDecimal.valueOf(3000.00),
            forDate = today,
            dayOfDeposit = 60,
            version = 1
        )
        val withdrawalEntity = WithdrawalEntity(
            id = 20,
            amount = BigDecimal.valueOf(150.00),
            date = today.minusDays(5),
            timeDeposit = depositEntity
        )
        depositEntity.withdrawals.add(withdrawalEntity)
        //when
        val domainWithWithdrawals = depositEntity.toDomainWithWithdrawals()
        //then
        assertThat(domainWithWithdrawals.timeDeposit.id).isEqualTo(5)
        assertThat(domainWithWithdrawals.timeDeposit.planType).isEqualTo("student")
        assertThat(domainWithWithdrawals.timeDeposit.balance).isEqualTo(3000.00)
        assertThat(domainWithWithdrawals.withdrawals.timeDeposit).isEqualTo(5)
        assertThat(domainWithWithdrawals.withdrawals.withdrawals).hasSize(1)
        assertThat(domainWithWithdrawals.withdrawals.withdrawals[0].id).isEqualTo(20)
        assertThat(domainWithWithdrawals.withdrawals.withdrawals[0].amount).isEqualTo(150.00)
        assertThat(domainWithWithdrawals.withdrawals.withdrawals[0].date).isEqualTo(today.minusDays(5))
    }

    @Test
    fun shouldMapWithdrawalEntitiesToDomain() {
        //given
        val depositEntity = TimeDepositEntity(
            id = 3,
            planType = "basic",
            balance = BigDecimal.valueOf(8000.00),
            forDate = today,
            dayOfDeposit = 120,
            version = 0
        )
        val entities = setOf(
            WithdrawalEntity(id = 1, amount = BigDecimal.valueOf(100.00), date = today.minusDays(10), timeDeposit = depositEntity),
            WithdrawalEntity(id = 2, amount = BigDecimal.valueOf(250.75), date = today.minusDays(5), timeDeposit = depositEntity)
        )
        //when
        val withdrawals = entities.toDomain(3)
        //then
        assertThat(withdrawals.timeDeposit).isEqualTo(3)
        assertThat(withdrawals.withdrawals).hasSize(2)
        val sortedWithdrawals = withdrawals.withdrawals.sortedBy { it.id }
        assertThat(sortedWithdrawals[0].id).isEqualTo(1)
        assertThat(sortedWithdrawals[0].amount).isEqualTo(100.00)
        assertThat(sortedWithdrawals[0].date).isEqualTo(today.minusDays(10))
        assertThat(sortedWithdrawals[1].id).isEqualTo(2)
        assertThat(sortedWithdrawals[1].amount).isEqualTo(250.75)
        assertThat(sortedWithdrawals[1].date).isEqualTo(today.minusDays(5))
    }

    @Test
    fun shouldMapEmptyWithdrawalEntitiesToDomain() {
        //given
        val entities = emptySet<WithdrawalEntity>()
        //when
        val withdrawals = entities.toDomain(1)
        //then
        assertThat(withdrawals.timeDeposit).isEqualTo(1)
        assertThat(withdrawals.withdrawals).isEmpty()
    }

    @Test
    fun shouldUpdateEntityFromDomainWithNextInterestDate() {
        //given
        val existingEntity = TimeDepositEntity(
            id = 4,
            planType = "basic",
            balance = BigDecimal.valueOf(5000.00),
            forDate = today.minusDays(1),
            dayOfDeposit = 90,
            lastInterestCalculationDate = today.minusDays(30),
            nextInterestCalculationDate = today,
            version = 3
        )
        val updatedDomain = TimeDeposit(
            id = 4,
            planType = "basic",
            balance = 5050.00,
            days = 91,
            forDate = today,
            nextInterestCalculationDate = today.plusMonths(1)
        )
        //when
        val updatedEntity = existingEntity.updateEntity(updatedDomain)
        //then
        assertThat(updatedEntity.balance).isEqualByComparingTo(BigDecimal("5050.0"))
        assertThat(updatedEntity.forDate).isEqualTo(today)
        assertThat(updatedEntity.dayOfDeposit).isEqualTo(91)
        assertThat(updatedEntity.lastInterestCalculationDate).isEqualTo(today)
        assertThat(updatedEntity.nextInterestCalculationDate).isEqualTo(today.plusMonths(1))
    }

    @Test
    fun shouldUpdateEntityFromDomainWithoutNextInterestDate() {
        //given
        val existingEntity = TimeDepositEntity(
            id = 4,
            planType = "basic",
            balance = BigDecimal.valueOf(5000.00),
            forDate = today.minusDays(1),
            dayOfDeposit = 90,
            lastInterestCalculationDate = today.minusDays(30),
            nextInterestCalculationDate = today,
            version = 3
        )
        val updatedDomain = TimeDeposit(
            id = 4,
            planType = "basic",
            balance = 5050.00,
            days = 91,
            forDate = today,
            nextInterestCalculationDate = null
        )
        //when
        val updatedEntity = existingEntity.updateEntity(updatedDomain)
        //then
        assertThat(updatedEntity.lastInterestCalculationDate).isEqualTo(today.minusDays(30))
        assertThat(updatedEntity.nextInterestCalculationDate).isEqualTo(today)
    }
}
