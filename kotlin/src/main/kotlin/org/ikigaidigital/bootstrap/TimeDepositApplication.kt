package org.ikigaidigital.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["org.ikigaidigital"])
class TimeDepositApplication

fun main(args: Array<String>) {
    runApplication<TimeDepositApplication>(*args)
}
