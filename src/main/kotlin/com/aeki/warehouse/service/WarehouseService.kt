package com.aeki.warehouse.service

import com.aeki.warehouse.model.Article
import com.aeki.warehouse.model.Product
import com.aeki.warehouse.model.ProductInfo
import com.aeki.warehouse.model.extensions.toEntity
import com.aeki.warehouse.model.extensions.toModel
import com.aeki.warehouse.repository.ArticleRepository
import com.aeki.warehouse.repository.ComponentRepository
import com.aeki.warehouse.repository.ProductRepository
import com.aeki.warehouse.repository.entity.ComponentEntity
import com.aeki.warehouse.repository.entity.ProductEntity
import com.aeki.warehouse.service.exception.ItemNotFoundException
import com.aeki.warehouse.service.exception.DuplicateItemException
import com.aeki.warehouse.service.exception.InsufficientStockException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class WarehouseService (
    private var productRepository: ProductRepository,
    private var articleRepository: ArticleRepository,
    private var componentRepository: ComponentRepository
) {

    open fun addArticle(article: Article) {
        val articleEntity = articleRepository.findById(article.id)

        if(articleEntity.isPresent) {
            val articleToUpdate = articleEntity.get()
            if(articleToUpdate.name == article.name) {
                articleToUpdate.stock += article.stock
                articleRepository.save(articleToUpdate)
            } else {
                throw DuplicateItemException("Article with ID ${article.id} already exists in the warehouse")
            }
        } else {
            articleRepository.save(article.toEntity())
        }
    }

    @Transactional
    open fun addArticles(articles: List<Article>) {
        try {
            articles.forEach { addArticle(it) }
        } catch (ex: DuplicateItemException) {
            throw DuplicateItemException("One or more items already exist in the warehouse")
        }
    }

    open fun retrieveArticles(): List<Article> {
        val articleEntities = articleRepository.findAll()
        return articleEntities.map { it.toModel() }
    }

    @Transactional
    open fun addProduct(productInfo: ProductInfo) {
        val product = productRepository.findByName(productInfo.name)
        if (product.isPresent) {
            throw DuplicateItemException("Product with name ${productInfo.name} already exists in the warehouse")
        }

        val newProduct = productRepository.save(ProductEntity(productInfo.name))
        productInfo.components.forEach { (articleID, amount) ->
            val article = articleRepository.findById(articleID)
            if (article.isPresent) {
                componentRepository.save(ComponentEntity(newProduct, article.get(), amount))
            } else {
                throw ItemNotFoundException("Product contains articles which are not available in the warehouse")
            }
        }
    }

    @Transactional
    open fun addProducts(products: List<ProductInfo>) {
        products.forEach { addProduct(it) }
    }

    @Transactional
    open fun sellProduct(productID: Long) {

        val productComponents = componentRepository.findByProductId(productID)
        if (productComponents.isEmpty()) {
            throw ItemNotFoundException("Product with ID $productID not found")
        }

        productComponents.forEach { component ->
            try {
                sellArticles(component.article.id, component.amount)
            } catch(e: InsufficientStockException) {
                throw InsufficientStockException("Product with ID $productID has insufficient stock")
            }
        }
    }

    open fun retrieveProducts(): List<Product> {

        val products = componentRepository.findAll()
                .groupBy({ it.product }, { Pair(it.article.id, it.amount) })
                .map { (product, components) ->
                    Product(product.id, ProductInfo(product.name, components.toMap()))
                }

        if (products.isEmpty()) {
            return products
        }

        val articlesStocks: HashMap<Long, Int> = articleRepository.findAll()
                .map { article -> article.id to article.stock }.toMap() as HashMap<Long, Int>

        products.forEach { product ->
            var minAvailability = Integer.MAX_VALUE
            for((articleID, articleAmount) in product.information.components) {
                val articleStock = articlesStocks[articleID]
                if(articleStock != null) {
                    val availabilityPerArticle = articleStock.div(articleAmount)
                    if (availabilityPerArticle <= minAvailability) {
                        minAvailability = availabilityPerArticle
                    }
                } else {
                    throw ItemNotFoundException("Failed to retrieve products information")
                }
                if(minAvailability == 0) break
            }
            product.stock = minAvailability
            product.isAvailable = (minAvailability > 0)
        }

        return products
    }

    @Transactional
    open fun sellArticles(articleID: Long, quantity: Int) {
        val article = articleRepository.findById(articleID)
        if (article.isEmpty) {
            throw ItemNotFoundException("Article with ID $articleID not found")
        }

        val availableStock = article.get().stock
        if(availableStock >= quantity) {
            article.get().stock = availableStock - quantity
            articleRepository.save(article.get())
        } else {
            throw InsufficientStockException(
                "Article with ID $articleID has insufficient stock (requested: $quantity, available: $availableStock)"
            )
        }
    }

}