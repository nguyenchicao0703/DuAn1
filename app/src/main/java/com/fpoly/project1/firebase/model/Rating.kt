package com.fpoly.project1.firebase.model

data class Rating(
    var id: String? = null,
    var customerId: String? = null,
    var sellerId: String? = null,
    var shipperId: String? = null,
    var productId: String? = null,
    var comment: String? = null,
    var date: String? = null,
    var rating: Long? = null
)
