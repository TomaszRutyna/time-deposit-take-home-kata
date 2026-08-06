package org.ikigaidigital.adapter.out.persistence.repository

import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TimeDepositRepository: JpaRepository<TimeDepositEntity, Int> {

}