package org.ikigaidigital.adapter.out.persistence.mapper

import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal
import org.ikigaidigital.domain.deposit.model.Withdrawals
import java.math.BigDecimal
import java.time.LocalDate

fun Withdrawal.toEntity(timeDeposit: TimeDepositEntity) =
    WithdrawalEntity(
        amount = BigDecimal.valueOf(amount),
        date = date,
        timeDeposit = timeDeposit,
    )

fun TimeDepositEntity.updateEntity(timeDeposit: TimeDeposit) =
    this.copy(
        balance = BigDecimal(timeDeposit.balance),
        forDate = timeDeposit.forDate?: LocalDate.now(),
        lastInterestCalculationDate = if (timeDeposit.nextInterestCalculationDate != null) this.nextInterestCalculationDate else this.lastInterestCalculationDate,
        nextInterestCalculationDate = if (timeDeposit.nextInterestCalculationDate != null) timeDeposit.nextInterestCalculationDate else this.nextInterestCalculationDate,
        dayOfDeposit = timeDeposit.days
    )

fun TimeDeposit.toEntity() =
    TimeDepositEntity(
        planType = this.planType,
        balance = BigDecimal.valueOf(this.balance),
        forDate = this.forDate?: LocalDate.now(),
        dayOfDeposit = this.days,
        nextInterestCalculationDate = this.nextInterestCalculationDate,
        version = 0
    )

fun TimeDepositEntity.toDomainWithWithdrawals() =
    TimeDepositWithWithdrawals(
        timeDeposit = this.toDomain(),
        withdrawals = this.withdrawals.toDomain(this.id!!)
    )

fun TimeDepositEntity.toDomain() =
    TimeDeposit(
        this.id,
        this.planType,
        this.balance.toDouble(),
        this.dayOfDeposit,
        this.forDate,
        this.nextInterestCalculationDate
    )

fun Set<WithdrawalEntity>.toDomain(timeDepositId: Int) =
    Withdrawals(
        timeDepositId,
        this.map {
            Withdrawal(
                it.id!!,
                it.amount.toDouble(),
                it.date
            )
        }
    )