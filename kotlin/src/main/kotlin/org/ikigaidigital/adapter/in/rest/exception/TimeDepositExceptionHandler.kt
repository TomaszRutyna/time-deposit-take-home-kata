package org.ikigaidigital.adapter.`in`.rest.exception

import org.ikigaidigital.domain.deposit.exception.TimeDepositNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class TimeDepositExceptionHandler {

    private val logger = LoggerFactory.getLogger(TimeDepositExceptionHandler::class.java)

    @ExceptionHandler(TimeDepositNotFoundException::class)
    fun notFoundException(e: TimeDepositNotFoundException): ResponseEntity<Void> {
        logger.warn("Resource not found: {}", e.message)
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleException(e: RuntimeException): ResponseEntity<Void> {
        logger.error("Unexpected error occurred", e)
        return ResponseEntity.internalServerError().build()
    }
}