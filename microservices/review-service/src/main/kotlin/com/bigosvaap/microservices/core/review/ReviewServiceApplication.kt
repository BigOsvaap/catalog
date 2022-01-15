package com.bigosvaap.microservices.core.review

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.bigosvaap")
class ReviewServiceApplication

fun main(args: Array<String>) {
	runApplication<ReviewServiceApplication>(*args)
}
