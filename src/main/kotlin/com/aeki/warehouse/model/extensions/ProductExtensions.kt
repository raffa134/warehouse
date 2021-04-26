package com.aeki.warehouse.model.extensions

import com.aeki.warehouse.model.Product
import com.aeki.warehouse.model.ProductContainArticlesDTO
import com.aeki.warehouse.model.ProductDTO
import com.aeki.warehouse.model.ProductInfo
import com.aeki.warehouse.model.ProductInfoDTO

fun ProductInfoDTO.toModel(): ProductInfo {
    val components = containArticles.map { it.artId to it.amountOf }.toMap()
    return ProductInfo(name, components)
}

fun Product.toDTO(): ProductDTO {
    val components = information.components.map { (articleID, articleAmount) ->
        ProductContainArticlesDTO(articleID, articleAmount)
    }
    return ProductDTO(id, information.name, isAvailable, stock, components)
}