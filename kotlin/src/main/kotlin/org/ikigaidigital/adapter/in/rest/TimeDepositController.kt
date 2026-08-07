package org.ikigaidigital.adapter.`in`.rest

import org.ikigaidigital.adapter.`in`.rest.mapper.toDomain
import org.ikigaidigital.adapter.`in`.rest.mapper.toResponse
import org.ikigaidigital.adapter.rest.api.TimeDepositApi
import org.ikigaidigital.adapter.rest.model.Pageable
import org.ikigaidigital.adapter.rest.model.TimeDepositPage
import org.ikigaidigital.adapter.rest.model.TimeDepositRequest
import org.ikigaidigital.port.`in`.FetchTimeDeposit
import org.ikigaidigital.port.`in`.UpsertTimeDeposit
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TimeDepositController(
    private val fetchTimeDeposit: FetchTimeDeposit,
    private val upsertTimeDeposit: UpsertTimeDeposit
): TimeDepositApi {

    private val logger = LoggerFactory.getLogger(TimeDepositController::class.java)

    override fun getTimeDeposits(pageable: Pageable?): ResponseEntity<TimeDepositPage> {
        val pageIndex = pageable?.page
        val pageSize = pageable?.propertySize
        logger.info("Fetching time deposits - pageIndex: {}, pageSize: {}", pageIndex, pageSize)

        val deposits = fetchTimeDeposit.fetchTimeDeposits(pageIndex, pageSize)
        logger.debug("Retrieved {} time deposits", deposits.size)

        return ResponseEntity.ok(
            TimeDepositPage(pageIndex, pageSize, deposits.map { it.toResponse() })
        )
    }

    override fun upsertTimeDeposit(timeDeposit: TimeDepositRequest): ResponseEntity<org.ikigaidigital.adapter.rest.model.TimeDepositResponse> {
        logger.info("Upsert time deposit request - id: {}, planType: {}, amount: {}, days: {}",
            timeDeposit.id, timeDeposit.planType, timeDeposit.amount, timeDeposit.days)

        val result = upsertTimeDeposit.upsertTimeDeposit(timeDeposit.toDomain()).toResponse()
        logger.info("Upsert time deposit completed - id: {}", result.id)

        return ResponseEntity.ok(result)
    }
}