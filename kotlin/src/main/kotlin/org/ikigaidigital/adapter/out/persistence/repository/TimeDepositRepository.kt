package org.ikigaidigital.adapter.out.persistence.repository

import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TimeDepositJpaRepository: JpaRepository<TimeDepositEntity, Int>, PagingAndSortingRepository<TimeDepositEntity, Int> {

    @Query("SELECT e FROM TimeDepositEntity e " +
            " LEFT JOIN FETCH e.withdrawals w ")
    fun findPageOfTimeDeposits(pageable: Pageable): Page<TimeDepositEntity>

    @Query("SELECT e FROM TimeDepositEntity e " +
            " LEFT JOIN FETCH e.withdrawals w WHERE e.id = :id ")
    fun findByIdWithWithdrawals(id: Int): TimeDepositEntity?
}