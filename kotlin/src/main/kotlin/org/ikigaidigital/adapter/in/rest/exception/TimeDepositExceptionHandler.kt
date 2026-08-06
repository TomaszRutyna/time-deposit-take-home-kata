package org.ikigaidigital.adapter.`in`.rest.exception

import org.ikigaidigital.adapter.out.persistence.exception.TimeDepositNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class TimeDepositExceptionHandler {

    @ExceptionHandler(TimeDepositNotFoundException::class)
    fun notFoundException(e: TimeDepositNotFoundException): ResponseEntity<Void> = ResponseEntity.notFound().build()

    @ExceptionHandler(RuntimeException::class)
    fun handleException(e: RuntimeException): ResponseEntity<Void> = ResponseEntity.internalServerError().build()
}