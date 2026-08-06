package org.ikigaidigital.adapter.out.persistence.repository

import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WithdrawalJpaRepository: JpaRepository<WithdrawalEntity, Int>