package com.bigosvaap.microservices.composite.product.services

import com.bigosvaap.api.core.product.Product
import com.bigosvaap.api.core.product.ProductService
import com.bigosvaap.api.core.recommendation.Recommendation
import com.bigosvaap.api.core.recommendation.RecommendationService
import com.bigosvaap.api.core.review.Review
import com.bigosvaap.api.core.review.ReviewService
import com.bigosvaap.api.exceptions.InvalidInputException
import com.bigosvaap.api.exceptions.NotFoundException
import com.bigosvaap.util.http.HttpErrorInfo
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.IOException

@Component
class ProductCompositeIntegration @Autowired constructor(
    private val restTemplate: RestTemplate,
    private val mapper: ObjectMapper,
    @Value("\${app.product-service.host}") productServiceHost: String,
    @Value("\${app.product-service.port}") productServicePort: Int,
    @Value("\${app.recommendation-service.host}") recommendationServiceHost: String,
    @Value("\${app.recommendation-service.port}") recommendationServicePort: Int,
    @Value("\${app.review-service.host}") reviewServiceHost: String,
    @Value("\${app.review-service.port}") reviewServicePort: Int
): ProductService, RecommendationService, ReviewService {

    private val productServiceUrl = "http://$productServiceHost:$productServicePort/product/"
    private val recommendationServiceUrl = "http://$recommendationServiceHost:$recommendationServicePort/recommendation?productId="
    private val reviewServiceUrl = "http://$reviewServiceHost:$reviewServicePort/review?productId="

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductCompositeIntegration::class.java)
    }

    override fun getProduct(productId: Int): Product? {
        return try {
            val url = productServiceUrl + productId
            LOG.debug("Will call getProduct API on URL: $url")
            val product = restTemplate.getForObject(url, Product::class.java)
            LOG.debug("Found a product with id: ${product?.productId}")
            product
        } catch (ex: HttpClientErrorException) {
            when (ex.statusCode) {
                HttpStatus.NOT_FOUND -> throw NotFoundException(getErrorMessage(ex))
                HttpStatus.UNPROCESSABLE_ENTITY -> throw InvalidInputException(getErrorMessage(ex))
                else -> {
                    LOG.warn("Got an unexpected HTTP error: ${ex.statusCode}, will rethrow it")
                    LOG.warn("Error body: ${ex.responseBodyAsString}")
                    throw ex
                }
            }
        }
    }

    private fun getErrorMessage(ex: HttpClientErrorException): String {
        return try {
            mapper.readValue(ex.responseBodyAsString, HttpErrorInfo::class.java).message
        } catch (ioEx: IOException) {
            ioEx.message ?: ""
        }
    }

    override fun getRecommendations(productId: Int): List<Recommendation> {
        return try {
            val url = recommendationServiceUrl + productId
            LOG.debug("Will call getRecommendations API on URL: $url")
            val recommendations = restTemplate.exchange(
                url,
                GET,
                null,
                object : ParameterizedTypeReference<List<Recommendation>>(){}).body.orEmpty()
            LOG.debug("Found ${recommendations.size} recommendations for a product with id: $productId")
            recommendations
        } catch (ex: Exception) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: ${ex.message}")
            arrayListOf()
        }
    }

    override fun getReviews(productId: Int): List<Review> {
        return try {
            val url = reviewServiceUrl + productId
            LOG.debug("Will call getReviews API on URL: $url")
            val reviews = restTemplate
                .exchange(url, GET, null, object : ParameterizedTypeReference<List<Review>>() {})
                .body.orEmpty()
            LOG.debug("Found ${reviews.size} reviews for a product with id: $productId")
            reviews
        } catch (ex: java.lang.Exception) {
            LOG.warn("Got an exception while requesting reviews, return zero reviews: ${ex.message}")
            arrayListOf()
        }
    }
}