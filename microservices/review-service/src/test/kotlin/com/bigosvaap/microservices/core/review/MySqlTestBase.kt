package com.bigosvaap.microservices.core.review

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer

abstract class MySqlTestBase {

    companion object {
        val database = MySQLContainer("mysql:5.7.32")

        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") {database.jdbcUrl}
            registry.add("spring.datasource.username") {database.username}
            registry.add("spring.datasource.password") {database.password}
        }

    }

    init {
        database.start()
    }

}