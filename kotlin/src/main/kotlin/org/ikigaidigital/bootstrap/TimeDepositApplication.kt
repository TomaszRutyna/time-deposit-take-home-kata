package org.ikigaidigital.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = ["org.ikigaidigital"])
@EntityScan(basePackages = ["org.ikigaidigital"])
@EnableJpaRepositories(basePackages = ["org.ikigaidigital"])
class TimeDepositApplication

fun main(args: Array<String>) {
    runApplication<TimeDepositApplication>(*args)
}
