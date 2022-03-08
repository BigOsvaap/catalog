package com.bigosvaap.microservices.core.review

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@ComponentScan("com.bigosvaap")
class ReviewServiceApplication

private val LOG = LoggerFactory.getLogger(ReviewServiceApplication::class.java)

fun main(args: Array<String>) {
	runApplication<ReviewServiceApplication>(*args)

	val context = SpringApplication.run(ReviewServiceApplication::class.java, *args)

	val mysqlUri = context.environment.getProperty("spring.datasource.url")
	LOG.info("Connected to MySQL: $mysqlUri")

}
