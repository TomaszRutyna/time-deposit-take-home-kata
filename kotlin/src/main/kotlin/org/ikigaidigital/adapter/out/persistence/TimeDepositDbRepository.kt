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
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(TimeDepositDbRepository::class.java)

    @Transactional
    override fun save(
        timeDeposit: TimeDeposit,
        withdrawal: Withdrawal?
    ): TimeDepositWithWithdrawals {
        val savedEntityId = if (timeDeposit.id != null) {
            val existingEntity = timeDepositJpaRepository.findByIdOrNull(timeDeposit.id)
                ?: throw TimeDepositNotFoundException(timeDeposit.id)

            val updatedEntity = timeDepositJpaRepository.save(existingEntity.updateEntity(timeDeposit))
            logger.debug("Updated time deposit entity id: {}", updatedEntity.id)

            withdrawal?.let {
                val savedWithdrawal = withdrawalJpaRepository.save(it.toEntity(updatedEntity))
                logger.debug("Saved withdrawal id: {} for deposit id: {}", savedWithdrawal.id, updatedEntity.id)
            }

            existingEntity.id
        } else {
            val savedEntity = timeDepositJpaRepository.save(timeDeposit.toEntity())
            logger.debug("Created new time deposit entity id: {}", savedEntity.id)
            savedEntity.id
        }

        return timeDepositJpaRepository.findByIdWithWithdrawals(savedEntityId!!)!!.toDomainWithWithdrawals()
    }

    override fun getTimeDeposit(id: Int) =
        timeDepositJpaRepository.findByIdOrNull(id)?.toDomain()

    override fun getTimeDeposits(
        pageIndex: Int?,
        pageSize: Int?
    ) = if (pageIndex != null && pageSize != null) {
        timeDepositJpaRepository.findPageOfTimeDeposits(
            PageRequest.of(pageIndex, pageSize, Sort.by("id"))
        ).content.map { it.toDomainWithWithdrawals() }
    } else {
        timeDepositJpaRepository.findAll().map { it.toDomainWithWithdrawals() }
    }

    override fun getTimeDepositsForInterestRecalculation(
        pageIndex: Int,
        pageSize: Int
    ): List<TimeDeposit> {
        val result = timeDepositJpaRepository.findByNextInterestCalculationDateNotAfter(
            LocalDate.now(),
            PageRequest.of(pageIndex, pageSize, Sort.by("id"))
        ).content.map { it.toDomain() }

        logger.debug("Found {} deposits eligible for interest recalculation (page: {})", result.size, pageIndex)
        return result
    }
}