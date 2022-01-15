package com.bigosvaap.microservices.composite.product.services

import com.bigosvaap.api.composite.product.*
import com.bigosvaap.api.core.product.Product
import com.bigosvaap.api.core.recommendation.Recommendation
import com.bigosvaap.api.core.review.Review
import com.bigosvaap.api.exceptions.NotFoundException
import com.bigosvaap.util.http.ServiceUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController


@RestController
class ProductCompositeServiceImpl @Autowired constructor(
    private val serviceUtil: ServiceUtil,
    private val integration: ProductCompositeIntegration): ProductCompositeService {

    override fun getProduct(productId: Int): ProductAggregate {
        val product: Product = integration.getProduct(productId)
            ?: throw NotFoundException("No product found for productId: $productId")

        val recommendations = integration.getRecommendations(productId)

        val reviews = integration.getReviews(productId)

        return createProductAggregate(product, recommendations, reviews, serviceUtil.serviceAddress)
    }

    private fun createProductAggregate(
        product: Product,
        recommendations: List<Recommendation>,
        reviews: List<Review>,
        serviceAddress: String
    ) : ProductAggregate {

        // 1. Setup product info
        val productId = product.productId
        val name = product.name
        val weight = product.weight

        // 2. Copy summary recommendation info, if available
        val recommendationSummaries = recommendations.
            map { RecommendationSummary(it.recommendationId, it.author, it.rate) }

        // 3. Copy summary review info, if available
        val reviewSummaries = reviews.
            map { ReviewSummary(it.reviewId, it.author, it.subject) }

        // 4. Create info regarding the involved microservices addresses
        val productAddress = product.serviceAddress
        val reviewAddress = if (reviews.isNotEmpty()) reviews[0].serviceAddress else ""
        val recommendationAddress =
            if (recommendations.isNotEmpty()) recommendations[0].serviceAddress else ""
        val serviceAddresses = ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress)

        return ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses)
    }
}