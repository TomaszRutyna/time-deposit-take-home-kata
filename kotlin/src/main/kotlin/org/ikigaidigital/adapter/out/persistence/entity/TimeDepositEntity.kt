package org.ikigaidigital.adapter.out.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "time_deposits")
data class TimeDepositEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Int? = null,
    private val planType: String,
    private val balance: BigDecimal,
    private val forDate: LocalDate,
    private val dayOfDeposit: Int,
    private val lastInterestCalculationDate: LocalDate? = null,
    private val nextInterestCalculationDate: LocalDate? = null,
    @Version
    private val version: Int? = null,
    @CreationTimestamp
    private val createdAt: Instant? = null,
    @UpdateTimestamp
    private val updatedAt: Instant? = null,
)