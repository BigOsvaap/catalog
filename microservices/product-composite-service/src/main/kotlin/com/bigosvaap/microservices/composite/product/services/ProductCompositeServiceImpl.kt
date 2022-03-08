package com.bigosvaap.microservices.composite.product.services

import com.bigosvaap.api.composite.product.*
import com.bigosvaap.api.core.product.Product
import com.bigosvaap.api.core.recommendation.Recommendation
import com.bigosvaap.api.core.review.Review
import com.bigosvaap.api.exceptions.NotFoundException
import com.bigosvaap.util.http.ServiceUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController


@RestController
class ProductCompositeServiceImpl @Autowired constructor(
    private val serviceUtil: ServiceUtil,
    private val integration: ProductCompositeIntegration): ProductCompositeService {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl::class.java)
    }

    override fun createProduct(body: ProductAggregate) {
        try {
            LOG.debug("createCompositeProduct: creates a new composite entity for productId: ${body.productId}")
            val product = Product(body.productId, body.name, body.weight, null)
            integration.createProduct(product)
            body.recommendations?.forEach { r ->
                val recommendation = Recommendation(
                    body.productId,
                    r.recommendationId,
                    r.author,
                    r.rate,
                    r.content,
                    null
                )
                integration.createRecommendation(recommendation)
            }
            body.reviews?.forEach { r ->
                val review =
                    Review(
                        body.productId,
                        r.reviewId,
                        r.author,
                        r.subject,
                        r.content,
                        null
                    )
                integration.createReview(review)
            }
            LOG.debug("createCompositeProduct: composite entities created for productId: ${body.productId}")
        } catch (re: RuntimeException) {
            LOG.warn("createCompositeProduct failed", re)
            throw re
        }
    }

    override fun getProduct(productId: Int): ProductAggregate {
        LOG.debug("getCompositeProduct: lookup a product aggregate for productId: $productId")

        val product = integration.getProduct(productId)
            ?: throw NotFoundException("No product found for productId: $productId")

        val recommendations = integration.getRecommendations(productId)

        val reviews = integration.getReviews(productId)

        LOG.debug("getCompositeProduct: aggregate entity found for productId: $productId")

        return createProductAggregate(product, recommendations, reviews, serviceUtil.serviceAddress)
    }

    override fun deleteProduct(productId: Int) {
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: $productId")

        integration.deleteProduct(productId)

        integration.deleteRecommendations(productId)

        integration.deleteReviews(productId)

        LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: $productId")
    }

    private fun createProductAggregate(product: Product,
                                       recommendations: List<Recommendation>,
                                       reviews: List<Review>,
                                       serviceAddress: String): ProductAggregate {
        // 1. Setup product info
        val productId = product.productId
        val name = product.name
        val weight = product.weight

        // 2. Copy summary recommendation info, if available
        val recommendationSummaries = recommendations.
            map { RecommendationSummary(it.recommendationId, it.author, it.rate, it.content) }

        // 3. Copy summary review info, if available
        val reviewSummaries = reviews.
            map { ReviewSummary(it.reviewId, it.author, it.subject, it.content) }

        // 4. Create info regarding the involved microservices addresses
        val productAddress = product.serviceAddress
        val reviewAddress = if (reviews.isNotEmpty()) reviews[0].serviceAddress else ""
        val recommendationAddress =
            if (recommendations.isNotEmpty()) recommendations[0].serviceAddress else ""
        val serviceAddresses = ServiceAddresses(serviceAddress, productAddress ?: "", reviewAddress ?: "", recommendationAddress ?: "")

        return ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses)
    }
}