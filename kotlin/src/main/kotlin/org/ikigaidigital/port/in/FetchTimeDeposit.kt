package org.ikigaidigital.port.`in`

import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals

interface FetchTimeDeposit {

    fun fetchTimeDeposits(pageIndex: Int? = null, pageSize: Int? = null): List<TimeDepositWithWithdrawals>
}