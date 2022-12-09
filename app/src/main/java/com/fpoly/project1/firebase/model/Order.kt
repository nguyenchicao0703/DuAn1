package com.fpoly.project1.firebase.model

data class Order(
    var id: String? = null,
    val customerId: String? = null,
    val date: String? = null,
    val status: Int? = null,
    val list: Map<String, Int>? = null
)
