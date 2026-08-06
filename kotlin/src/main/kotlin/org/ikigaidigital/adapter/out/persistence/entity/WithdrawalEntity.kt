package org.ikigaidigital.adapter.out.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "withdrawals")
data class WithdrawalEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Int? = null,
    private val planType: String,
    private val amount: BigDecimal,
    private val date: LocalDate,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id")
    private val timeDeposit: TimeDepositEntity,
    @CreationTimestamp
    private val createdAt: Instant? = null
)