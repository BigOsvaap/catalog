package com.bigosvaap.microservices.core.recommendation

import com.bigosvaap.api.core.recommendation.Recommendation
import com.bigosvaap.microservices.core.recommendation.services.RecommendationMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers


class MapperTests {

    private val mapper = Mappers.getMapper(RecommendationMapper::class.java)

    @Test
    fun mapperTests() {
        assertNotNull(mapper)
        val api = Recommendation(1, 2, "a", 4, "C", "adr")
        val entity = mapper.apiToEntity(api)
        assertEquals(api.productId, entity.productId)
        assertEquals(api.recommendationId, entity.recommendationId)
        assertEquals(api.author, entity.author)
        assertEquals(api.rate, entity.rating)
        assertEquals(api.content, entity.content)
        val (productId, recommendationId, author, rate, content, serviceAddress) = mapper.entityToApi(entity)
        assertEquals(api.productId, productId)
        assertEquals(api.recommendationId, recommendationId)
        assertEquals(api.author, author)
        assertEquals(api.rate, rate)
        assertEquals(api.content, content)
        assertNull(serviceAddress)
    }

    @Test
    fun mapperListTests() {
        assertNotNull(mapper)
        val api = Recommendation(1, 2, "a", 4, "C", "adr")
        val apiList: List<Recommendation> = listOf(api)
        val entityList = mapper.apiListToEntityList(apiList)
        assertEquals(apiList.size, entityList.size)
        val entity = entityList[0]
        assertEquals(api.productId, entity.productId)
        assertEquals(api.recommendationId, entity.recommendationId)
        assertEquals(api.author, entity.author)
        assertEquals(api.rate, entity.rating)
        assertEquals(api.content, entity.content)
        val api2List = mapper.entityListToApiList(entityList)
        assertEquals(apiList.size, api2List.size)
        val (productId, recommendationId, author, rate, content, serviceAddress) = api2List[0]
        assertEquals(api.productId, productId)
        assertEquals(api.recommendationId, recommendationId)
        assertEquals(api.author, author)
        assertEquals(api.rate, rate)
        assertEquals(api.content, content)
        assertNull(serviceAddress)
    }

}