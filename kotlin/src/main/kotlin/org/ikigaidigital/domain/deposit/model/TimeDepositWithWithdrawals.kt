package org.ikigaidigital.domain.deposit.model

data class TimeDepositWithWithdrawals(
    val timeDeposit: TimeDeposit,
    val withdrawals: Withdrawals
)