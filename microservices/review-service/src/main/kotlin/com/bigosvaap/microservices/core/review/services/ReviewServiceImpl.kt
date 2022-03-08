package com.bigosvaap.microservices.core.review.services

import com.bigosvaap.api.core.review.Review
import com.bigosvaap.api.core.review.ReviewService
import com.bigosvaap.api.exceptions.InvalidInputException
import com.bigosvaap.microservices.core.review.persistence.ReviewRepository
import com.bigosvaap.util.http.ServiceUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.bind.annotation.RestController

@RestController
class ReviewServiceImpl @Autowired constructor(
    private val serviceUtil: ServiceUtil,
    private val repository: ReviewRepository,
    private val mapper: ReviewMapper
    ): ReviewService {

    companion object {
        private val LOG = LoggerFactory.getLogger(ReviewServiceImpl::class.java)
    }

    override fun getReviews(productId: Int): List<Review> {
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }

        val entityList = repository.findByProductId(productId)
        val list = mapper.entityListToApiList(entityList)
        list.forEach{ review ->
            review.serviceAddress = serviceUtil.serviceAddress
        }

        LOG.debug("getReviews: response size: ${list.size}")

        return list
    }

    override fun createReview(body: Review): Review {
        return try {
            val entity = mapper.apiToEntity(body)
            val newEntity = repository.save(entity)
            LOG.debug("createReview: created a review entity: ${body.productId}/${body.reviewId}")
            mapper.entityToApi(newEntity)
        } catch (dive: DataIntegrityViolationException) {
            throw InvalidInputException("Duplicate key, Product Id: ${body.productId}, Review Id: ${body.reviewId}")
        }
    }

    override fun deleteReviews(productId: Int) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: $productId")
        repository.deleteAll(repository.findByProductId(productId))
    }

}