package org.ikigaidigital.adapter.out.persistence.config

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(basePackages = ["org.ikigaidigital.adapter.out.persistence"])
@EnableJpaRepositories(basePackages = ["org.ikigaidigital.adapter.out.persistence"])
class DatabaseConfiguration {
}