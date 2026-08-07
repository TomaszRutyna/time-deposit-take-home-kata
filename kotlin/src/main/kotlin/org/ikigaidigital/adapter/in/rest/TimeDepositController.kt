package org.ikigaidigital.adapter.`in`.rest

import org.ikigaidigital.adapter.`in`.rest.mapper.toDomain
import org.ikigaidigital.adapter.`in`.rest.mapper.toResponse
import org.ikigaidigital.adapter.rest.api.TimeDepositApi
import org.ikigaidigital.adapter.rest.model.Pageable
import org.ikigaidigital.adapter.rest.model.TimeDepositPage
import org.ikigaidigital.adapter.rest.model.TimeDepositRequest
import org.ikigaidigital.port.`in`.FetchTimeDeposit
import org.ikigaidigital.port.`in`.UpsertTimeDeposit
import org.springframework.context.annotation.Primary
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@Primary
@RestController
class TimeDepositController(
    private val fetchTimeDeposit: FetchTimeDeposit,
    private val upsertTimeDeposit: UpsertTimeDeposit
): TimeDepositApi {
    override fun getTimeDeposits(pageable: Pageable?): ResponseEntity<TimeDepositPage> {
        val pageIndex = pageable?.page
        val pageSize = pageable?.propertySize

        return ResponseEntity.ok(
            TimeDepositPage(
                pageIndex,
                pageSize,
                fetchTimeDeposit.fetchTimeDeposits(pageIndex, pageSize).map { it.toResponse() }
            )
        )
    }

    override fun upsertTimeDeposit(timeDeposit: TimeDepositRequest) =
        ResponseEntity.ok(upsertTimeDeposit.upsertTimeDeposit(timeDeposit.toDomain()).toResponse())
}