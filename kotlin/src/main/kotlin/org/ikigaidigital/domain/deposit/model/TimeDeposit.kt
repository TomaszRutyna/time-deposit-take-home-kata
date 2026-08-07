package org.ikigaidigital.domain.deposit.model

import java.time.LocalDate

data class TimeDeposit(
    val id: Int? = null,
    val planType: String,
    //it should be BigDecimal, but due to requirements I cannot change it
    var balance: Double,
    val days: Int,
    var forDate: LocalDate? = null,
    var nextInterestCalculationDate: LocalDate? = null
)