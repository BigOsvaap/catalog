package com.bigosvaap.microservices.core.recommendation.services

import com.bigosvaap.api.core.recommendation.Recommendation
import com.bigosvaap.api.core.recommendation.RecommendationService
import com.bigosvaap.api.exceptions.InvalidInputException
import com.bigosvaap.util.http.ServiceUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class RecommendationServiceImpl @Autowired constructor(private val serviceUtil: ServiceUtil) : RecommendationService {

    companion object {
        private val LOG = LoggerFactory.getLogger(RecommendationServiceImpl::class.java)
    }

    override fun getRecommendations(productId: Int): List<Recommendation> {
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }

        if (productId == 113) {
            LOG.debug("No recommendations found for productId: $productId", )
            return arrayListOf()
        }

        val list = arrayListOf<Recommendation>()
        list.add(Recommendation(productId, 1, "Author 1", 1, "Content 1", serviceUtil.serviceAddress))
        list.add(Recommendation(productId, 2, "Author 2", 2, "Content 2", serviceUtil.serviceAddress))
        list.add(Recommendation(productId, 3, "Author 3", 3, "Content 3", serviceUtil.serviceAddress))

        LOG.debug("/recommendation response size: ${list.size}")

        return list
    }
}