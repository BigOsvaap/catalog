package com.bigosvaap.microservices.core.recommendation.services

import com.bigosvaap.api.core.recommendation.Recommendation
import com.bigosvaap.api.core.recommendation.RecommendationService
import com.bigosvaap.api.exceptions.InvalidInputException
import com.bigosvaap.microservices.core.recommendation.persistence.RecommendationRepository
import com.bigosvaap.util.http.ServiceUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.web.bind.annotation.RestController


@RestController
class RecommendationServiceImpl @Autowired constructor(
    private val serviceUtil: ServiceUtil,
    private val repository: RecommendationRepository,
    private val mapper: RecommendationMapper
    ) : RecommendationService {

    companion object {
        private val LOG = LoggerFactory.getLogger(RecommendationServiceImpl::class.java)
    }

    override fun getRecommendations(productId: Int): List<Recommendation> {
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }

        val entityList = repository.findByProductId(productId)
        val list = mapper.entityListToApiList(entityList)
        list.forEach { recommendation ->
            recommendation.serviceAddress = serviceUtil.serviceAddress
        }

        LOG.debug("getRecommendations: response size: ${list.size}")

        return list
    }

    override fun createRecommendation(body: Recommendation): Recommendation {
        return try {
            val entity = mapper.apiToEntity(body)
            val newEntity = repository.save(entity)
            LOG.debug("createRecommendation: created a recommendation entity: ${body.productId}/${body.recommendationId}")
            mapper.entityToApi(newEntity)
        } catch (dke: DuplicateKeyException) {
            throw InvalidInputException("Duplicate key, Product Id: ${body.productId}, Recommendation Id: ${body.recommendationId}")
        }
    }

    override fun deleteRecommendations(productId: Int) {
        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: $productId")
        repository.deleteAll(repository.findByProductId(productId))
    }

}