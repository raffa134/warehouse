package com.aeki.warehouse.repository.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "COMPONENT")
@IdClass(ComponentId::class)
class ComponentEntity(

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    var product: ProductEntity,

    @Id
    @ManyToOne
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    var article: ArticleEntity,

    @Column(name = "amount")
    var amount: Int
)

@Embeddable
class ComponentId (
    var product: Long,
    var article: Long
): Serializable