package com.aeki.warehouse.model.extensions

import com.aeki.warehouse.model.Article
import com.aeki.warehouse.model.ArticleDTO
import com.aeki.warehouse.repository.entity.ArticleEntity

fun Article.toEntity() = ArticleEntity(id, name, stock)
fun Article.toDTO() = ArticleDTO(id, name, stock)
fun ArticleDTO.toModel() = Article(artId, name, stock)
fun ArticleEntity.toModel() = Article(id, name, stock)