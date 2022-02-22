package com.bigosvaap.microservices.core.recommendation

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer

abstract class MongoDbTestBase {

    companion object {
        val database = MongoDBContainer("mongo:4.4.2")

        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host") { database.containerIpAddress }
            registry.add("spring.data.mongodb.port") {
                database.getMappedPort(
                    27017
                )
            }
            registry.add("spring.data.mongodb.database") { "test" }
        }
    }

    init {
        database.start()
    }

}