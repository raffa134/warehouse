package com.aeki.warehouse.controller

import com.aeki.warehouse.model.ArticleDTO
import com.aeki.warehouse.model.ArticleInventoryDTO
import com.aeki.warehouse.model.ProductInfoDTO
import com.aeki.warehouse.model.ProductInventoryDTO
import com.aeki.warehouse.model.ProductsInfoDTO
import com.aeki.warehouse.model.extensions.toDTO
import com.aeki.warehouse.model.extensions.toModel
import com.aeki.warehouse.service.WarehouseService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@Validated
open class WarehouseController(
    private var warehouseService: WarehouseService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @PostMapping("/articles")
    open fun addArticle(@RequestBody @Valid articleDTO: ArticleDTO): ResponseEntity<Any> {
        logger.info("Request received to store an article with ID ${articleDTO.artId}")
        warehouseService.addArticle(articleDTO.toModel())
        return ResponseEntity(HttpStatus.CREATED)
    }

    @GetMapping("/articles")
    open fun retrieveArticles(): ResponseEntity<ArticleInventoryDTO> {
        logger.info("Request received to retrieve all articles")
        val articles = warehouseService.retrieveArticles()
        return ResponseEntity(ArticleInventoryDTO(articles.map { it.toDTO() }), HttpStatus.OK)
    }

    @PostMapping("/articles/file")
    open fun addArticles(
            @RequestParam("file") inventoryFile: MultipartFile
    ): ResponseEntity<Any> {
        logger.info("Request received to upload articles via file")
        val inputStream = inventoryFile.inputStream
        val articlesDTO =
                jacksonObjectMapper().readValue(inputStream, ArticleInventoryDTO::class.java)
        val articles = articlesDTO.inventory.map { it.toModel() }
        warehouseService.addArticles(articles)
        inputStream.close()
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PostMapping("/products")
    @Transactional
    open fun addProduct(@RequestBody @Valid productInfoDTO: ProductInfoDTO): ResponseEntity<Any> {
        logger.info("Request received to add a new product")
        warehouseService.addProduct(productInfoDTO.toModel())
        return ResponseEntity(HttpStatus.CREATED)
    }

    @GetMapping("/products")
    open fun getProducts(): ResponseEntity<ProductInventoryDTO> {
        logger.info("Request received to retrieve all products")
        val products = warehouseService.retrieveProducts()
        return ResponseEntity(ProductInventoryDTO(products.map { it.toDTO() }), HttpStatus.OK)
    }

    @PostMapping("/products/{id}/sell")
    @Transactional
    open fun sellProduct(@PathVariable id: Long): ResponseEntity<Any> {
        logger.info("Request received to sell product with ID $id")
        warehouseService.sellProduct(id)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/products/file")
    @Transactional
    open fun addProducts(
            @RequestParam("file") productsFile: MultipartFile
    ): ResponseEntity<Any> {
        logger.info("Request received to upload products via file")
        val inputStream = productsFile.inputStream
        val productsInfoDTO =
                jacksonObjectMapper().readValue(inputStream, ProductsInfoDTO::class.java)
        val productsInfo = productsInfoDTO.products.map { it.toModel() }
        warehouseService.addProducts(productsInfo)
        inputStream.close()
        return ResponseEntity(HttpStatus.CREATED)
    }

}