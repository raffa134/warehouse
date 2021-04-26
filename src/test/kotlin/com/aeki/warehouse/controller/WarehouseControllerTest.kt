package com.aeki.warehouse.controller

import com.aeki.warehouse.service.WarehouseService
import com.aeki.warehouse.service.exception.DuplicateItemException
import com.aeki.warehouse.service.exception.InsufficientStockException
import com.aeki.warehouse.service.exception.ItemNotFoundException
import com.aeki.warehouse.utils.DataGenerator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringJUnitConfig
@WebMvcTest(controllers = [WarehouseController::class])
class WarehouseControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var warehouseService: WarehouseService

    companion object {
        private const val EXCEPTION_MESSAGE = "Exception message"
        private const val SEVERITY_ERROR = "error"
        private const val PRODUCT_ID = 42
    }

    @Test
    fun `should submit a POST request with a valid article successfully`() {
        val article =
                ClassPathResource("jsonPayloads/correctPayloads/article.json").file.readText()

        every { warehouseService.addArticle(any()) } returns Unit

        this.mockMvc.post("/articles") {
            content = article
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isCreated() }
                }
    }

    @Test
    fun `should return an error when trying to add an article with an invalid payload`() {
        val article =
                ClassPathResource("jsonPayloads/correctPayloads/article.json").file.readText()

        // Payload with wrong syntax is obtained by removing a bracket from correct JSON representation
        val wrongSyntaxPayload = article.substring(1)

        this.mockMvc.post("/articles") {
            content = wrongSyntaxPayload
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") {
                        value(RestExceptionHandler.GENERIC_INPUT_ERROR_MSG)
                    }
                }
    }

    @Test
    fun `should return an error when trying to add an article without mandatory parameter`() {

        val article = ClassPathResource(
                "jsonPayloads/wrongPayloads/articleMissingParameter.json"
                ).file.readText()

        this.mockMvc.post("/articles") {
            content = article
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") {
                        value(RestExceptionHandler.GENERIC_INPUT_ERROR_MSG)
                    }
                }
    }

    @Test
    fun `should return an error when trying to add an article with invalid parameter`() {

        val article = ClassPathResource(
                "jsonPayloads/wrongPayloads/articleInvalidParameter.json"
        ).file.readText()

        this.mockMvc.post("/articles") {
            content = article
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") {
                        value(RestExceptionHandler.GENERIC_INPUT_ERROR_MSG)
                    }
                }
    }

    @Test
    fun `should submit a POST request with a valid product successfully`() {
        val product =
                ClassPathResource("jsonPayloads/correctPayloads/product.json").file.readText()

        every { warehouseService.addProduct(any()) } returns Unit

        this.mockMvc.post("/products") {
            content = product
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isCreated() }
                }
    }

    @Test
    fun `should return an error when trying to add a product with an invalid payload`() {

        val product =
                ClassPathResource("jsonPayloads/correctPayloads/product.json").file.readText()

        // Payload with wrong syntax is obtained by removing a bracket from correct JSON representation
        val wrongSyntaxPayload = product.substring(1)

        this.mockMvc.post("/products") {
            content = wrongSyntaxPayload
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") {
                        value(RestExceptionHandler.GENERIC_INPUT_ERROR_MSG)
                    }
                }
    }

    @Test
    fun `should return an error when trying to add a product without mandatory parameter`() {

        val product = ClassPathResource(
                "jsonPayloads/wrongPayloads/productMissingParameter.json"
        ).file.readText()

        this.mockMvc.post("/products") {
            content = product
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") {
                        value(RestExceptionHandler.GENERIC_INPUT_ERROR_MSG)
                    }
                }
    }

    @Test
    fun `should return an error when trying to add a product with invalid parameter`() {

        val product = ClassPathResource(
                "jsonPayloads/wrongPayloads/productInvalidParameter.json"
        ).file.readText()

        this.mockMvc.post("/articles") {
            content = product
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") {
                        value(RestExceptionHandler.GENERIC_INPUT_ERROR_MSG)
                    }
                }
    }

    @Test
    fun `should upload a list of articles from file correctly`() {

        val inventoryFile =
                ClassPathResource("jsonPayloads/correctPayloads/articlesInventory.json").file

        every { warehouseService.addArticles(any()) } returns Unit

        this.mockMvc.perform (
            MockMvcRequestBuilders
                .multipart("/articles/file")
                .file("file", inventoryFile.readBytes())
        )
                .andExpect {
                    status().isCreated
                }

    }

    @Test
    fun `should upload a list of products from file correctly`() {

        val productsFile =
                ClassPathResource("jsonPayloads/correctPayloads/productsFile.json").file

        every { warehouseService.addProducts(any()) } returns Unit

        this.mockMvc.perform (
            MockMvcRequestBuilders
                    .multipart("/products/file")
                    .file("file", productsFile.readBytes())
        )
                .andExpect {
                    status().isCreated
                }

    }

    @Test
    fun `should return an error when trying to add an article which already exists`() {
        val article =
                ClassPathResource("jsonPayloads/correctPayloads/article.json").file.readText()

        every {
            warehouseService.addArticle(any())
        } throws DuplicateItemException(EXCEPTION_MESSAGE)

        this.mockMvc.post("/articles") {
            content = article
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") { value(EXCEPTION_MESSAGE) }
                }
    }

    @Test
    fun `should return an error when trying to add a product which already exists`() {
        val product =
                ClassPathResource("jsonPayloads/correctPayloads/product.json").file.readText()

        every {
            warehouseService.addProduct(any())
        } throws DuplicateItemException(EXCEPTION_MESSAGE)

        this.mockMvc.post("/products") {
            content = product
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") { value(EXCEPTION_MESSAGE) }
                }
    }

    @Test
    fun `should return an error when trying to add a product with insufficient stock`() {
        val product =
                ClassPathResource("jsonPayloads/correctPayloads/product.json").file.readText()

        every {
            warehouseService.addProduct(any())
        } throws InsufficientStockException(EXCEPTION_MESSAGE)

        this.mockMvc.post("/products") {
            content = product
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") { value(EXCEPTION_MESSAGE) }
                }
    }

    @Test
    fun `should return an error when trying to sell a non-existent product`() {

        every {
            warehouseService.sellProduct(any())
        } throws ItemNotFoundException(EXCEPTION_MESSAGE)

        this.mockMvc.post("/products/{id}/sell", PRODUCT_ID)
                .andDo {
                    print()
                }
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") { value(EXCEPTION_MESSAGE) }
                }
    }

    @Test
    fun `should return an error when trying to sell a product with insufficient stock`() {

        every {
            warehouseService.sellProduct(any())
        } throws InsufficientStockException(EXCEPTION_MESSAGE)

        every {
            warehouseService.sellArticles(any(), any())
        } returns Unit

        this.mockMvc.post("/products/{id}/sell", PRODUCT_ID)
                .andDo {
                    print()
                }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.severity") { value(SEVERITY_ERROR) }
                    jsonPath("$.message") { value(EXCEPTION_MESSAGE) }
                }
    }

    @Test
    fun `should retrieve a list of articles`() {

        every {
            warehouseService.retrieveArticles()
        } returns listOf(DataGenerator.article1, DataGenerator.article2)

        this.mockMvc.get("/articles")
                .andDo {
                    print()
                }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.inventory.length()") { value(2) }
                    jsonPath("$.inventory[0].art_id") { value(DataGenerator.article1.id) }
                    jsonPath("$.inventory[0].name") { value(DataGenerator.article1.name) }
                    jsonPath("$.inventory[0].stock") { value(DataGenerator.article1.stock) }
                    jsonPath("$.inventory[1].art_id") { value(DataGenerator.article2.id) }
                    jsonPath("$.inventory[1].name") { value(DataGenerator.article2.name) }
                    jsonPath("$.inventory[1].stock") { value(DataGenerator.article2.stock) }
                }
    }

    @Test
    fun `should retrieve a list of products`() {

        every {
            warehouseService.retrieveProducts()
        } returns listOf(DataGenerator.product1)

        this.mockMvc.get("/products")
                .andDo {
                    print()
                }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.products.length()") { value(1) }
                    jsonPath("$.products[0].id") { value(DataGenerator.product1.id) }
                    jsonPath("$.products[0].name") { value(DataGenerator.product1.information.name) }
                    jsonPath("$.products[0].is_available") { value(DataGenerator.product1.isAvailable) }
                    jsonPath("$.products[0].stock") { value(DataGenerator.product1.stock) }
                    jsonPath("$.products[0].contain_articles.length()") { value(2) }
                }
    }

}