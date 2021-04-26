package com.aeki.warehouse.utils

import com.aeki.warehouse.model.Article
import com.aeki.warehouse.model.Product
import com.aeki.warehouse.model.ProductInfo

object DataGenerator {

    val article1 = Article(1L, "leg", 20)
    val article2 = Article(2L, "screw", 20)
    val article3 = Article(3L, "shelf", 3)

    val product1Info =
            ProductInfo("Marius Stool", mapOf(article1.id to 4, article2.id to 20))
    val product2Info =
            ProductInfo("Billy Bookshelf", mapOf(article3.id to 10))

    val product1 = Product(
            1L,
            product1Info,
            true,
            1
    )

}