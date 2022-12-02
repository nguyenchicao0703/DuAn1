package com.fpoly.project1.firebase.model

class Product(
	var __id: String,
	val sellerId: String? = null,
	val categoryId: String? = null,
	val name: String? = null,
	val description: String? = null,
	val price: String? = null,
	val thumbnails: List<String>? = null
)
