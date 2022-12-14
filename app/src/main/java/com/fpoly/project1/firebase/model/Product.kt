package com.fpoly.project1.firebase.model

data class Product(
    var id: String? = null,
    val sellerId: String? = null,
    var categoryId: String? = null,
    var name: String? = null,
    var description: String? = null,
    var price: Long? = null,
    var sales: Long? = null,
    var thumbnails: List<String>? = null
)
