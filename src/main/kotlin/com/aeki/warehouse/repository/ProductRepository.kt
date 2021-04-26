package com.aeki.warehouse.repository

import com.aeki.warehouse.repository.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductRepository : JpaRepository<ProductEntity, Long> {
    fun findByName(name: String): Optional<ProductEntity>
}
