package org.ikigaidigital.adapter.out.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "time_deposits")
data class TimeDepositEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val planType: String,
    val balance: BigDecimal,
    val forDate: LocalDate,
    val dayOfDeposit: Int,
    val lastInterestCalculationDate: LocalDate? = null,
    val nextInterestCalculationDate: LocalDate? = null,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "timeDeposit", cascade = [CascadeType.ALL])
    val withdrawals: MutableSet<WithdrawalEntity> = mutableSetOf(),
    @Version
    val version: Int? = null,
    @CreationTimestamp
    val createdAt: Instant? = null,
    @UpdateTimestamp
    val updatedAt: Instant? = null,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as TimeDepositEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}

