package com.bigosvaap.microservices.core.product.services

import com.bigosvaap.api.core.product.Product
import com.bigosvaap.api.core.product.ProductService
import com.bigosvaap.api.exceptions.InvalidInputException
import com.bigosvaap.api.exceptions.NotFoundException
import com.bigosvaap.util.http.GlobalControllerExceptionHandler
import com.bigosvaap.util.http.ServiceUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController


@RestController
class ProductServiceImpl @Autowired constructor(private val serviceUtil: ServiceUtil) : ProductService {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductServiceImpl::class.java)
    }

    override fun getProduct(productId: Int): Product {
        LOG.debug("/product return the found product for productId=$productId")

        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }

        if (productId == 13) {
            throw NotFoundException("No product found for productId: $productId")
        }

        return Product(productId, "name-$productId", 123, serviceUtil.serviceAddress)

    }
}