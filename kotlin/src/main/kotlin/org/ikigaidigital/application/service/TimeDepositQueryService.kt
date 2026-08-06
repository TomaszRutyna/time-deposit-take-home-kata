package org.ikigaidigital.application.service

import org.ikigaidigital.port.`in`.FetchTimeDeposit
import org.ikigaidigital.port.out.TimeDepositRepository
import org.springframework.stereotype.Service

@Service
class TimeDepositQueryService(
    private val timeDepositRepository: TimeDepositRepository
): FetchTimeDeposit {
    override fun fetchTimeDeposits(pageSize: Int?, pageIndex: Int?) = timeDepositRepository.getTimeDeposits(pageSize, pageIndex)
}