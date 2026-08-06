package org.ikigaidigital.adapter.`in`.rest.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class TimeDepositExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun handleException(e: RuntimeException): ResponseEntity<Void> = ResponseEntity.internalServerError().build()
}