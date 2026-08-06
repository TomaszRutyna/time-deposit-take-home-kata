package org.ikigaidigital.adapter.`in`.rest.mapper

import org.ikigaidigital.adapter.rest.model.TimeDepositRequest
import org.ikigaidigital.adapter.rest.model.TimeDepositResponse
import org.ikigaidigital.adapter.rest.model.WithdrawalResponse
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal
import org.ikigaidigital.domain.deposit.model.Withdrawals
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneOffset

fun TimeDepositRequest.toDomain() =
    TimeDeposit(
        this.id,
        this.planType,
        this.amount.toDouble(),
        this.days,
        LocalDate.now()
    )

fun TimeDepositWithWithdrawals.toResponse(): TimeDepositResponse {
    return TimeDepositResponse(
        this.timeDeposit.id!!,
        BigDecimal.valueOf(this.timeDeposit.balance),
        this.timeDeposit.days,
        this.timeDeposit.planType,
        this.withdrawals.toResponse()
    )
}

private fun Withdrawals.toResponse() =
    this.withdrawals.map { it.toResponse()}

private fun Withdrawal.toResponse() =
    WithdrawalResponse(
        this.id!!,
        BigDecimal.valueOf(this.amount),
        this.date
    )