package com.fpoly.project1.firebase.model

data class Order(
	var __id: String,
	val customerId: String,
	val date: String,
	val status: Int,
	val list: Map<String, Int>
)