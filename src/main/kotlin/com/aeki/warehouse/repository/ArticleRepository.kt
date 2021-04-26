package com.aeki.warehouse.repository

import com.aeki.warehouse.repository.entity.ArticleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<ArticleEntity, Long>
