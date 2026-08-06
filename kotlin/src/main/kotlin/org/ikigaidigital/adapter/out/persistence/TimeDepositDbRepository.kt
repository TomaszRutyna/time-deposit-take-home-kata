package org.ikigaidigital.adapter.out.persistence

import org.ikigaidigital.adapter.out.persistence.exception.TimeDepositNotFoundException
import org.ikigaidigital.adapter.out.persistence.mapper.toDomain
import org.ikigaidigital.adapter.out.persistence.mapper.toDomainWithWithdrawals
import org.ikigaidigital.adapter.out.persistence.mapper.toEntity
import org.ikigaidigital.adapter.out.persistence.mapper.updateEntity
import org.ikigaidigital.adapter.out.persistence.repository.TimeDepositJpaRepository
import org.ikigaidigital.adapter.out.persistence.repository.WithdrawalJpaRepository
import org.ikigaidigital.domain.deposit.model.TimeDeposit
import org.ikigaidigital.domain.deposit.model.TimeDepositWithWithdrawals
import org.ikigaidigital.domain.deposit.model.Withdrawal
import org.ikigaidigital.port.out.TimeDepositRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class TimeDepositDbRepository(
    private val timeDepositJpaRepository: TimeDepositJpaRepository,
    private val withdrawalJpaRepository: WithdrawalJpaRepository,
) : TimeDepositRepository {

    @Transactional
    override fun save(
        timeDeposit: TimeDeposit,
        withdrawal: Withdrawal?
    ): TimeDepositWithWithdrawals {
        val storedTimeDepositId = if (timeDeposit.id != null) {
            val storedTimeDepositId = timeDepositJpaRepository.findByIdOrNull(timeDeposit.id)
                ?: throw TimeDepositNotFoundException(timeDeposit.id)

            val updatedTimeDeposit = timeDepositJpaRepository.save(storedTimeDepositId.updateEntity(timeDeposit))

            withdrawal?.let {
                withdrawalJpaRepository.save(it.toEntity(updatedTimeDeposit))
            }

            storedTimeDepositId.id
        } else {
            timeDepositJpaRepository.save(timeDeposit.toEntity()).id
        }

        return timeDepositJpaRepository.findByIdWithWithdrawals(storedTimeDepositId!!)!!.toDomainWithWithdrawals()
    }

    override fun getTimeDeposit(id: Int) =
        timeDepositJpaRepository.findByIdOrNull(id)?.toDomain()

    override fun getTimeDeposits(
        pageSize: Int?,
        pageIndex: Int?
    ) = if (pageSize != null && pageIndex != null) {
        timeDepositJpaRepository.findPageOfTimeDeposits(
            PageRequest.of(pageSize, pageIndex, Sort.by("id"))
        ).content.map { it.toDomainWithWithdrawals() }
    } else {
        timeDepositJpaRepository.findAll().map { it.toDomainWithWithdrawals() }
    }

    override fun getTimeDepositsForInterestRecalculation(
        pageSize: Int,
        pageIndex: Int
    ) = timeDepositJpaRepository.findByNextInterestCalculationDateNotAfter(
        LocalDate.now(),
        PageRequest.of(pageSize, pageIndex, Sort.by("id"))
    )
        .content
        .map { it.toDomain() }
}