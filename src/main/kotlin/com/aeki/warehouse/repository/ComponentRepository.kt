package com.aeki.warehouse.repository

import com.aeki.warehouse.repository.entity.ComponentEntity
import com.aeki.warehouse.repository.entity.ComponentId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ComponentRepository : JpaRepository<ComponentEntity, ComponentId> {
    fun findByProductId(productID: Long): List<ComponentEntity>
}
 