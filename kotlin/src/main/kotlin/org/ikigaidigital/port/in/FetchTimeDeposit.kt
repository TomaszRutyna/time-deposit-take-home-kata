package org.ikigaidigital.port.`in`

import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals

interface FetchTimeDeposit {

    fun fetchTimeDeposits(pageSize: Int? = null, pageIndex: Int? = null): List<TimeDepositWithWithdrawals>
}