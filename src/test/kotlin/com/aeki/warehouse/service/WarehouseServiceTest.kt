package com.aeki.warehouse.service

import com.aeki.warehouse.service.exception.DuplicateItemException
import com.aeki.warehouse.service.exception.InsufficientStockException
import com.aeki.warehouse.service.exception.ItemNotFoundException
import com.aeki.warehouse.utils.DataGenerator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.annotation.DirtiesContext

@DataJpaTest
@ComponentScan("com.aeki.warehouse.service")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
open class WarehouseServiceTest {

    @Autowired
    private lateinit var warehouseService: WarehouseService

    @BeforeEach
    fun init() {

        // Add product's components
        val articles = listOf(DataGenerator.article1, DataGenerator.article2)
        warehouseService.addArticles(articles)

        // Add product
        val productInfo = DataGenerator.product1Info
        warehouseService.addProduct(productInfo)

    }

    @Test
    open fun `should store and retrieve articles correctly`() {

        val retrievedArticles = warehouseService.retrieveArticles()

        assert(retrievedArticles.first() == DataGenerator.article1)
        assert(retrievedArticles.last() == DataGenerator.article2)

    }

    @Test
    open fun `should update existing article stock correctly`() {

        warehouseService.addArticle(DataGenerator.article1)
        val updatedArticle = warehouseService.retrieveArticles().first()

        assert(updatedArticle.stock == DataGenerator.article1.stock*2)

    }

    @Test
    open fun `should throw exception when trying to add an article with existing ID`() {

        assertThrows<DuplicateItemException> {
            warehouseService.addArticles(
                    listOf(DataGenerator.article1.copy(name = "Different name"))
            )
        }

    }

    @Test
    open fun `should store and retrieve products correctly`() {

        val product = warehouseService.retrieveProducts().first()
        assert(product == DataGenerator.product1)

    }

    @Test
    open fun `should throw exception when trying to add a product with unavailable articles`() {

        assertThrows<ItemNotFoundException>{
            warehouseService.addProduct(DataGenerator.product2Info)
        }

    }

    @Test
    open fun `should throw exception when trying to add a product with existing name`() {

        assertThrows<DuplicateItemException> {
            warehouseService.addProduct(DataGenerator.product1Info)
        }

    }

    @Test
    open fun `should sell a product and update inventory correctly`() {

        val product = warehouseService.retrieveProducts().first()
        val articles = warehouseService.retrieveArticles()

        val productPreviousStock = product.stock
        val articlesPreviousStocks = mapOf(
            articles.first().id to articles.first().stock,
            articles.last().id to articles.last().stock
        )

        warehouseService.sellProduct(product.id)

        val updatedProduct = warehouseService.retrieveProducts().first()
        assert(updatedProduct.stock == productPreviousStock-1)

        val updatedArticles = warehouseService.retrieveArticles()
        val articlesNewStocks = mapOf(
            updatedArticles.first().id to updatedArticles.first().stock,
            updatedArticles.last().id to updatedArticles.last().stock
        )

        val productComponents = product.information.components
        productComponents.forEach { (articleID, articleAmount) ->
            assert(articlesNewStocks[articleID] ==
                    articlesPreviousStocks[articleID]?.minus(articleAmount))
        }

    }

    @Test
    open fun `should throw exception when trying to sell a product with insufficient stock`() {

        val product = warehouseService.retrieveProducts().first()
        warehouseService.sellProduct(product.id)
        val updatedProduct = warehouseService.retrieveProducts().first()

        assert(!updatedProduct.isAvailable)
        assertThrows<InsufficientStockException> {
            warehouseService.sellProduct(product.id)
        }
    }


}