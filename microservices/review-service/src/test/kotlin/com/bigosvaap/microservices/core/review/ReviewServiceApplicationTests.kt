package com.bigosvaap.microservices.core.review

import com.bigosvaap.api.core.review.Review
import com.bigosvaap.microservices.core.review.persistence.ReviewRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec
import reactor.core.publisher.Mono.just


@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto = update"])
class ReviewServiceApplicationTests: MySqlTestBase(){

    @Autowired private lateinit var client: WebTestClient
    @Autowired private lateinit var repository: ReviewRepository

    @BeforeEach
    fun setupDb() {
        repository.deleteAll()
    }

    @Test
    fun getReviewsByProductId() {
        val productId = 1

        assertEquals(0, repository.findByProductId(productId).size)

        postAndVerifyReview(productId, 1, OK)
        postAndVerifyReview(productId, 2, OK)
        postAndVerifyReview(productId, 3, OK)

        assertEquals(3, repository.findByProductId(productId).size)

        getAndVerifyReviewsByProductId(productId, OK)
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[2].productId").isEqualTo(productId)
            .jsonPath("$[2].reviewId").isEqualTo(3)
    }

    @Test
    fun duplicateError() {
        val productId = 1
        val reviewId = 1
        assertEquals(0, repository.count())
        postAndVerifyReview(productId, reviewId, OK)
            .jsonPath("$.productId").isEqualTo(productId)
            .jsonPath("$.reviewId").isEqualTo(reviewId)
        assertEquals(1, repository.count())
        postAndVerifyReview(productId, reviewId, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id: 1")
        assertEquals(1, repository.count())
    }

    @Test
    fun deleteReviews() {
        val productId = 1
        val reviewId = 1
        postAndVerifyReview(productId, reviewId, OK)
        assertEquals(1, repository.findByProductId(productId).size)
        deleteAndVerifyReviewsByProductId(productId, OK)
        assertEquals(0, repository.findByProductId(productId).size)
        deleteAndVerifyReviewsByProductId(productId, OK)
    }

    @Test
    fun getReviewsMissingParameter() {
        getAndVerifyReviewsByProductId("", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present")
    }

    @Test
    fun getReviewsInvalidParameter() {
        getAndVerifyReviewsByProductId("?productId=no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Type mismatch.")
    }

    @Test
    fun getReviewsNotFound() {
        getAndVerifyReviewsByProductId("?productId=213", OK)
            .jsonPath("$.length()").isEqualTo(0)
    }

    @Test
    fun getReviewsInvalidParameterNegativeValue() {
        val productIdInvalid = -1
        getAndVerifyReviewsByProductId("?productId=$productIdInvalid", UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Invalid productId: $productIdInvalid")
    }


    private fun getAndVerifyReviewsByProductId(productId: Int, expectedStatus: HttpStatus): BodyContentSpec {
        return getAndVerifyReviewsByProductId("?productId=$productId", expectedStatus)
    }

    private fun getAndVerifyReviewsByProductId(productIdQuery: String, expectedStatus: HttpStatus): BodyContentSpec {
        return client.get()
            .uri("/review$productIdQuery")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
    }

    private fun postAndVerifyReview(productId: Int, reviewId: Int, expectedStatus: HttpStatus): BodyContentSpec {
        val review = Review(
            productId, reviewId,
            "Author $reviewId", "Subject $reviewId", "Content $reviewId", "SA"
        )
        return client.post()
            .uri("/review")
            .body(just(review), Review::class.java)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
    }

    private fun deleteAndVerifyReviewsByProductId(productId: Int, expectedStatus: HttpStatus): BodyContentSpec {
        return client.delete()
            .uri("/review?productId=$productId")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .expectBody()
    }

}
