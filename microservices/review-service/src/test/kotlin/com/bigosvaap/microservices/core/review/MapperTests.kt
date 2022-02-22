package com.bigosvaap.microservices.core.review

import com.bigosvaap.api.core.review.Review
import com.bigosvaap.microservices.core.review.persistence.ReviewEntity
import com.bigosvaap.microservices.core.review.services.ReviewMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers

class MapperTests {

    private val mapper: ReviewMapper = Mappers.getMapper(ReviewMapper::class.java)

    @Test
    fun mapperTests() {
        assertNotNull(mapper)
        val api = Review(1, 2, "a", "s", "C", "adr")
        val entity: ReviewEntity = mapper.apiToEntity(api)
        assertEquals(api.productId, entity.productId)
        assertEquals(api.reviewId, entity.reviewId)
        assertEquals(api.author, entity.author)
        assertEquals(api.subject, entity.subject)
        assertEquals(api.content, entity.content)
        val (productId, reviewId, author, subject, content, serviceAddress) = mapper.entityToApi(entity)
        assertEquals(api.productId, productId)
        assertEquals(api.reviewId, reviewId)
        assertEquals(api.author, author)
        assertEquals(api.subject, subject)
        assertEquals(api.content, content)
        assertNull(serviceAddress)
    }

    @Test
    fun mapperListTests() {
        assertNotNull(mapper)
        val api = Review(1, 2, "a", "s", "C", "adr")
        val apiList: List<Review> = listOf(api)
        val entityList: List<ReviewEntity> = mapper.apiListToEntityList(apiList)
        assertEquals(apiList.size, entityList.size)
        val entity = entityList[0]
        assertEquals(api.productId, entity.productId)
        assertEquals(api.reviewId, entity.reviewId)
        assertEquals(api.author, entity.author)
        assertEquals(api.subject, entity.subject)
        assertEquals(api.content, entity.content)
        val api2List: List<Review> = mapper.entityListToApiList(entityList)
        assertEquals(apiList.size, api2List.size)
        val (productId, reviewId, author, subject, content, serviceAddress) = api2List[0]
        assertEquals(api.productId, productId)
        assertEquals(api.reviewId, reviewId)
        assertEquals(api.author, author)
        assertEquals(api.subject, subject)
        assertEquals(api.content, content)
        assertNull(serviceAddress)
    }

}