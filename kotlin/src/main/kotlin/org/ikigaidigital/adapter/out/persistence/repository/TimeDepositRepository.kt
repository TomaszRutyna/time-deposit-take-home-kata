package org.ikigaidigital.adapter.out.persistence.repository

import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TimeDepositJpaRepository: JpaRepository<TimeDepositEntity, Int>, PagingAndSortingRepository<TimeDepositEntity, Int> {

    @Query("SELECT e.id FROM TimeDepositEntity e")
    fun findPageOfIds(pageable: Pageable): Page<Int>

    @Query("SELECT e FROM TimeDepositEntity e LEFT JOIN FETCH e.withdrawals WHERE e.id IN :ids")
    fun findByIdsWithWithdrawals(ids: List<Int>): List<TimeDepositEntity>

    @Query("SELECT e FROM TimeDepositEntity e " +
            " LEFT JOIN FETCH e.withdrawals w WHERE e.id = :id ")
    fun findByIdWithWithdrawals(id: Int): TimeDepositEntity?

    @Query("SELECT e FROM TimeDepositEntity e WHERE e.nextInterestCalculationDate <= :date")
    fun findByNextInterestCalculationDateNotAfter(date: LocalDate, pageable: Pageable): Page<TimeDepositEntity>
}