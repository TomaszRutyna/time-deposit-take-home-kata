package org.ikigaidigital.port.`in`

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.BaseIntegrationTest
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.LocalDate

class InterestRecalculationTest: BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        timeDepositRepository.deleteAll()
    }

    @Test
    fun shouldRecalculateInterests() {
        //given
        val storedStudentTimeDeposit = timeDepositRepository.save(
            TimeDepositEntity(
                planType = "student",
                dayOfDeposit = 70,
                balance = BigDecimal.valueOf(1000),
                forDate = LocalDate.now().minusDays(10),
                nextInterestCalculationDate = LocalDate.now().minusDays(1)
            )
        )
        val storedPremiumTimeDeposit = timeDepositRepository.save(
            TimeDepositEntity(
                planType = "premium",
                dayOfDeposit = 80,
                balance = BigDecimal.valueOf(2000),
                forDate = LocalDate.now().minusDays(10),
                nextInterestCalculationDate = LocalDate.now()
            )
        )
        val storedPremiumTimeDepositNotValidForRecalculation = timeDepositRepository.save(
            TimeDepositEntity(
                planType = "premium",
                dayOfDeposit = 1,
                balance = BigDecimal.valueOf(3000),
                forDate = LocalDate.now().minusDays(10),
                nextInterestCalculationDate = LocalDate.now()
            )
        )
        val storedBasicimeDepositNotValidForRecalculation = timeDepositRepository.save(
            TimeDepositEntity(
                planType = "basic",
                dayOfDeposit = 50,
                balance = BigDecimal.valueOf(5000),
                forDate = LocalDate.now().minusDays(10),
                nextInterestCalculationDate = LocalDate.now().plusDays(1)
            )
        )
        //when
        interestRecalculation.recalculateInterests()
        //then
        val changedStudentDeposit = timeDepositRepository.findByIdOrNull(storedStudentTimeDeposit.id!!)
        assertThat(changedStudentDeposit).isNotNull
        assertThat(changedStudentDeposit?.balance).isEqualTo(BigDecimal.valueOf(1002.50).setScale(2))
        assertThat(changedStudentDeposit?.forDate).isEqualTo(LocalDate.now())
        assertThat(changedStudentDeposit?.nextInterestCalculationDate).isEqualTo(LocalDate.now().plusMonths(1))

        val changedPremiumDeposit = timeDepositRepository.findByIdOrNull(storedPremiumTimeDeposit.id!!)
        assertThat(changedPremiumDeposit).isNotNull
        assertThat(changedPremiumDeposit?.balance).isEqualTo(BigDecimal.valueOf(2008.33).setScale(2))
        assertThat(changedPremiumDeposit?.forDate).isEqualTo(LocalDate.now())
        assertThat(changedPremiumDeposit?.nextInterestCalculationDate).isEqualTo(LocalDate.now().plusMonths(1))

        val unchangedPremiumDeposit = timeDepositRepository.findByIdOrNull(storedPremiumTimeDepositNotValidForRecalculation.id!!)
        assertThat(unchangedPremiumDeposit).isNotNull
        assertThat(unchangedPremiumDeposit?.balance).isEqualTo(storedPremiumTimeDepositNotValidForRecalculation.balance.setScale(2))
        assertThat(unchangedPremiumDeposit?.nextInterestCalculationDate).isEqualTo(storedPremiumTimeDepositNotValidForRecalculation.nextInterestCalculationDate)

        val unchangedBasicDeposit = timeDepositRepository.findByIdOrNull(storedBasicimeDepositNotValidForRecalculation.id!!)
        assertThat(unchangedBasicDeposit).isNotNull
        assertThat(unchangedBasicDeposit?.balance).isEqualTo(storedBasicimeDepositNotValidForRecalculation.balance.setScale(2))
        assertThat(unchangedBasicDeposit?.nextInterestCalculationDate).isEqualTo(storedBasicimeDepositNotValidForRecalculation.nextInterestCalculationDate)
    }

}