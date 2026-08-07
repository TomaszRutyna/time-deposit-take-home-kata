package org.ikigaidigital.port.out

import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal

interface TimeDepositRepository {

    fun save(timeDeposit: TimeDeposit, withdrawal: Withdrawal? = null): TimeDepositWithWithdrawals

    fun getTimeDeposit(id: Int): TimeDeposit?

    fun getTimeDeposits(pageIndex: Int? = null, pageSize: Int? = null): List<TimeDepositWithWithdrawals>

    fun getTimeDepositsForInterestRecalculation(pageIndex: Int, pageSize: Int): List<TimeDeposit>
}