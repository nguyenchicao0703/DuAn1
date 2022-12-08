package com.fpoly.project1.firebase.model

data class Product(
    var id: String? = null,
    val sellerId: String? = null,
    val categoryId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Long? = null,
    val thumbnails: List<String>? = null
)
