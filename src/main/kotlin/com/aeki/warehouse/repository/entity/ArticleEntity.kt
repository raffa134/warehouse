package com.aeki.warehouse.repository.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "ARTICLE")
data class ArticleEntity(

    @Id
    @Column(name = "id")
    var id: Long,

    @Column(name = "name")
    var name: String,

    @Column(name = "stock")
    var stock: Int

)