package org.ikigaidigital.domain.deposit.model

import java.time.LocalDate

data class Withdrawals(
    val timeDeposit: Int,
    val withdrawals: List<Withdrawal>
)
data class Withdrawal(
    val id: Int? = null,
    val amount: Double,
    val date: LocalDate
)