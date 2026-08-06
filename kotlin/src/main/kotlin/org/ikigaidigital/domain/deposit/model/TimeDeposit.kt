package org.ikigaidigital.domain.deposit.model

import java.time.LocalDate

data class TimeDeposit(
    val id: Int,
    val planType: String,
    var balance: Double,
    val days: Int,
    var forDate: LocalDate? = null
)