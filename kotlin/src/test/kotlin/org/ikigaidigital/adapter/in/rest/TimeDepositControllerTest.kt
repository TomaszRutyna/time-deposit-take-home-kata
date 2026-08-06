package org.ikigaidigital.adapter.`in`.rest

import org.assertj.core.api.Assertions.assertThat
import org.ikigaidigital.BaseIntegrationTest
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity
import org.ikigaidigital.adapter.rest.model.TimeDepositPage
import org.ikigaidigital.adapter.rest.model.TimeDepositRequest
import org.ikigaidigital.adapter.rest.model.TimeDepositResponse
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.math.BigDecimal
import java.time.LocalDate

class TimeDepositControllerTest: BaseIntegrationTest() {

    companion object {
        const val API_URL = "/time-deposit"
    }

    @Test
    fun shouldStoreNewTimeDepositInDb() {
        //given
        val request = TimeDepositRequest(
            planType = "student",
            days = 1,
            amount = BigDecimal.valueOf(1000)
        )
        //when
        val responseString = mockMvc.perform(
            MockMvcRequestBuilders.put(API_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andReturn().response.contentAsString
        //then
        val response = objectMapper.readValue(responseString, TimeDepositResponse::class.java)

        assertThat(response.id).isNotNull
        assertThat(response.planType).isEqualTo(request.planType)
        assertThat(response.days).isEqualTo(request.days)
        assertThat(response.amount.toInt()).isEqualTo(request.amount.toInt())

        assertThat(response.withdrawals).isEmpty()

        val timeDeposit = timeDepositRepository.findByIdOrNull(response.id!!)
        assertThat(timeDeposit?.forDate).isNotNull
        assertThat(timeDeposit?.nextInterestCalculationDate).isNotNull
    }

    @Test
    fun shouldUpdateTimeDepositInDb() {
        //given
        val storedTimeDeposit = timeDepositRepository.save(
            TimeDepositEntity(
                planType = "student",
                dayOfDeposit = 1,
                balance = BigDecimal.valueOf(1000),
                forDate = LocalDate.now().minusDays(10),
                nextInterestCalculationDate = LocalDate.now().plusDays(20)
            )
        )

        val request = TimeDepositRequest(
            id = storedTimeDeposit.id,
            planType = "student",
            days = 11,
            amount = BigDecimal.valueOf(900)
        )
        //when

        val responseString = mockMvc.perform(
            MockMvcRequestBuilders.put(API_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andReturn().response.contentAsString
        //then
        val response = objectMapper.readValue(responseString, TimeDepositResponse::class.java)

        assertThat(response.id).isEqualTo(request.id!!)
        assertThat(response.planType).isEqualTo(request.planType)
        assertThat(response.days).isEqualTo(request.days)
        assertThat(response.amount.toInt()).isEqualTo(request.amount.toInt())

        val timeDeposit = timeDepositRepository.findByIdWithWithdrawals(response.id!!)
        assertThat(timeDeposit?.forDate).isEqualTo(LocalDate.now())
        assertThat(timeDeposit?.nextInterestCalculationDate).isEqualTo(storedTimeDeposit.nextInterestCalculationDate)

        val withdrawals = timeDeposit?.withdrawals
        assertThat(withdrawals).hasSize(1)
        val withdrawal = withdrawals?.iterator()?.next()

        assertThat(withdrawal?.amount?.toInt()).isEqualTo(100)
        assertThat(withdrawal?.date).isEqualTo(LocalDate.now())
    }

    @Test
    fun shouldFetchTimeDeposits() {
        //given
        val storedStudentTimeDeposit = timeDepositRepository.save(
            TimeDepositEntity(
                planType = "student",
                dayOfDeposit = 1,
                balance = BigDecimal.valueOf(1000),
                forDate = LocalDate.now().minusDays(10),
                nextInterestCalculationDate = LocalDate.now().plusDays(20)
            )
        )

        val storedPremiumTimeDeposit = timeDepositRepository.save(
            TimeDepositEntity(
                planType = "premium",
                dayOfDeposit = 1,
                balance = BigDecimal.valueOf(2000),
                forDate = LocalDate.now().minusDays(10),
                nextInterestCalculationDate = LocalDate.now().plusDays(20)
            )
        )
        //when
        val responseString = mockMvc.perform(
            MockMvcRequestBuilders.get(API_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("size", "10")
                .queryParam("page", "1")
        ).andReturn().response.contentAsString
        //then
        val response = objectMapper.readValue(responseString, TimeDepositPage::class.java)

        response.content?.first { it.id == storedPremiumTimeDeposit.id }!!.assertWithEntity(storedPremiumTimeDeposit)
        response.content?.first { it.id == storedStudentTimeDeposit.id }!!.assertWithEntity(storedStudentTimeDeposit)
    }

    private fun TimeDepositResponse.assertWithEntity(timeDepositEntity: TimeDepositEntity) {
        assertThat(this.id).isEqualTo(timeDepositEntity.id!!)
        assertThat(this.planType).isEqualTo(timeDepositEntity.planType)
        assertThat(this.days).isEqualTo(timeDepositEntity.dayOfDeposit)
        assertThat(this.amount.toInt()).isEqualTo(timeDepositEntity.balance.toInt())
    }
}
