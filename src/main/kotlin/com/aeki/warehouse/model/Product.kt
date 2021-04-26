package com.aeki.warehouse.model

data class Product (
    val id: Long,
    val information: ProductInfo,
    var isAvailable: Boolean = false,
    var stock: Int = 0,
)

data class ProductInfo (
    val name: String,
    val components: Map<Long, Int> = emptyMap()
)
