package com.bigosvaap.microservices.core.product.services

import com.bigosvaap.api.core.product.Product
import com.bigosvaap.api.core.product.ProductService
import com.bigosvaap.api.exceptions.InvalidInputException
import com.bigosvaap.api.exceptions.NotFoundException
import com.bigosvaap.microservices.core.product.persistence.ProductRepository
import com.bigosvaap.util.http.ServiceUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.web.bind.annotation.RestController


@RestController
class ProductServiceImpl @Autowired constructor(
    private val serviceUtil: ServiceUtil,
    private val repository: ProductRepository,
    private val mapper: ProductMapper
    ) : ProductService {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductServiceImpl::class.java)
    }

    override fun getProduct(productId: Int): Product {
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }

        val entity = repository.findByProductId(productId) ?:
            throw NotFoundException("No product found for productId: $productId")

        val response = mapper.entityToApi(entity).apply {
            serviceAddress = serviceUtil.serviceAddress
        }

        LOG.debug("getProduct: found productId: ${response.productId}")

        return response

    }

    override fun createProduct(body: Product): Product {
        try {
            val entity = mapper.apiToEntity(body)
            val newEntity = repository.save(entity)

            LOG.debug("createProduct: entity created for productId: ${body.productId}")
            return mapper.entityToApi(newEntity)
        } catch (ex: DuplicateKeyException) {
            throw InvalidInputException("Duplicate key, Product Id: ${body.productId}")
        }
    }

    override fun deleteProduct(productId: Int) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: $productId")
        repository.findByProductId(productId)?.let(repository::delete)
    }

}