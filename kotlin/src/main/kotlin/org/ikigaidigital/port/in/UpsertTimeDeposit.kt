package org.ikigaidigital.port.`in`

import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals

interface UpsertTimeDeposit {
    fun upsertTimeDeposit(timeDeposit: TimeDeposit): TimeDepositWithWithdrawals
}