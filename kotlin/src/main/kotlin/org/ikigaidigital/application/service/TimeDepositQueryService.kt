package org.ikigaidigital.application.service

import org.ikigaidigital.port.`in`.FetchTimeDeposit
import org.ikigaidigital.port.out.TimeDepositRepository
import org.springframework.stereotype.Service

@Service
class TimeDepositQueryService(
    private val timeDepositRepository: TimeDepositRepository
): FetchTimeDeposit {
    override fun fetchTimeDeposits(pageIndex: Int?, pageSize: Int?) = timeDepositRepository.getTimeDeposits(pageIndex, pageSize)
}