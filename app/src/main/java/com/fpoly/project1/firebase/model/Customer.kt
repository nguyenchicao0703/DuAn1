package com.fpoly.project1.firebase.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Customer(
	var __id: String? = null,
	var gid: String? = null,
	var fid: String? = null,
	var avatarUrl: String? = null,
	var fullName: String? = null,
	var birthDate: String? = null,
	var emailAddress: String? = null,
	var phoneNumber: String? = null,
	var postalAddress: String? = null,
	var favoriteIds: List<String>? = null
)
