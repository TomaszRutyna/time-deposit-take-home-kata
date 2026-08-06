package org.ikigaidigital.port.out

import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal

interface TimeDepositRepository {

    fun save(timeDeposit: TimeDeposit, withdrawal: Withdrawal? = null): TimeDepositWithWithdrawals

    fun getTimeDeposit(id: Int): TimeDeposit?

    fun getTimeDeposits(pageSize: Int? = null, pageIndex: Int? = null): List<TimeDepositWithWithdrawals>

    fun getTimeDepositsForInterestRecalculation(pageSize: Int, pageIndex: Int): List<TimeDeposit>
}