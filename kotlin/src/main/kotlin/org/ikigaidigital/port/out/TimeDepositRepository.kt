package org.ikigaidigital.port.out

import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal
import java.time.LocalDate

interface TimeDepositRepository {

    fun save(timeDeposit: TimeDeposit, nextInterestCalculationDate: LocalDate? = null, withdrawal: Withdrawal? = null): TimeDepositWithWithdrawals

    fun getTimeDeposit(id: Int): TimeDeposit?

    fun getTimeDeposits(pageSize: Int? = null, pageIndex: Int? = null): List<TimeDepositWithWithdrawals>
}