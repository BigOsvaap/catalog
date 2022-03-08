package com.bigosvaap.microservices.core.product

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@ComponentScan("com.bigosvaap")
class ProductServiceApplication

private val LOG = LoggerFactory.getLogger(ProductServiceApplication::class.java)

fun main(args: Array<String>) {
	runApplication<ProductServiceApplication>(*args)

	val context = SpringApplication.run(ProductServiceApplication::class.java, *args)
	val mongodDbHost = context.environment.getProperty("spring.data.mongodb.host")
	val mongodDbPort = context.environment.getProperty("spring.data.mongodb.port")
	LOG.info("Connected to MongoDb: $mongodDbHost : $mongodDbPort")

}
