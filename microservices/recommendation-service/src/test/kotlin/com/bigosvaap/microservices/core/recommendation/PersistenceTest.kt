package com.bigosvaap.microservices.core.recommendation

import com.bigosvaap.microservices.core.recommendation.persistence.RecommendationEntity
import com.bigosvaap.microservices.core.recommendation.persistence.RecommendationRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.test.context.TestPropertySource


@DataMongoTest(excludeAutoConfiguration = [EmbeddedMongoAutoConfiguration::class])
@TestPropertySource(properties = ["spring.data.mongodb.auto-index-creation = true"])
class PersistenceTest : MongoDbTestBase() {

    @Autowired
    private lateinit var repository: RecommendationRepository
    private lateinit var savedEntity: RecommendationEntity

    @BeforeEach
    fun setupDb() {
        repository.deleteAll()

        val entity = RecommendationEntity(1, 2, "a", 3, "c")
        savedEntity = repository.save(entity)

        assertEqualsRecommendation(entity, savedEntity)
    }

    @Test
    fun create() {
        val newEntity = RecommendationEntity(1, 3, "a", 3, "c")
        repository.save(newEntity)

        val foundEntity = repository.findById(newEntity.id!!).get()
        assertEqualsRecommendation(newEntity, foundEntity)

        assertEquals(2, repository.count())
    }

    @Test
    fun update() {
        savedEntity.author = "a2"
        repository.save(savedEntity)

        val foundEntity = repository.findById(savedEntity.id!!).get()
        assertEquals(1, foundEntity.version!!.toLong())
        assertEquals("a2", foundEntity.author)
    }

    @Test
    fun delete() {
        repository.delete(savedEntity)
        assertFalse(repository.existsById(savedEntity.id!!))
    }

    @Test
    fun getByProductId() {
        val entityList = repository.findByProductId(savedEntity.productId)
        assertThat(entityList, hasSize(1))
        assertEqualsRecommendation(savedEntity, entityList[0])
    }

    @Test
    fun duplicateError() {
        assertThrows(DuplicateKeyException::class.java) {
            val entity = RecommendationEntity(1, 2, "a", 3, "c")
            repository.save(entity)
        }
    }

    @Test
    fun optimisticLockError() {
        // Store the saved entity in two separate entity objects
        // Store the saved entity in two separate entity objects
        val entity1 = repository.findById(savedEntity.id!!).get()
        val entity2 = repository.findById(savedEntity.id!!).get()

        // Update the entity using the first entity object

        // Update the entity using the first entity object
        entity1.author = "a1"
        repository.save(entity1)

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version number, i.e. an Optimistic Lock Error

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version number, i.e. an Optimistic Lock Error
        assertThrows(OptimisticLockingFailureException::class.java) {
            entity2.author = "a2"
            repository.save(entity2)
        }

        // Get the updated entity from the database and verify its new sate

        // Get the updated entity from the database and verify its new sate
        val updatedEntity = repository.findById(savedEntity.id!!).get()
        assertEquals(1, updatedEntity.version as Int)
        assertEquals("a1", updatedEntity.author)
    }

    private fun assertEqualsRecommendation(expectedEntity: RecommendationEntity, actualEntity: RecommendationEntity) {
        assertEquals(expectedEntity.id, actualEntity.id)
        assertEquals(expectedEntity.version, actualEntity.version)
        assertEquals(expectedEntity.productId, actualEntity.productId)
        assertEquals(expectedEntity.recommendationId, actualEntity.recommendationId)
        assertEquals(expectedEntity.author, actualEntity.author)
        assertEquals(expectedEntity.rating, actualEntity.rating)
        assertEquals(expectedEntity.content, actualEntity.content)
    }

}