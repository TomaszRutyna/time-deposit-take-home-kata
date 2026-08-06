package org.ikigaidigital.domain.plan.model

import java.math.BigDecimal

data class DepositPlanConstraints(
    val firstInterestCalculationDay: Int? = null,
    val lastInterestCalculationDay: Int? = null,
    val interestRate: BigDecimal,
    val numberOfDecimals: Int
)