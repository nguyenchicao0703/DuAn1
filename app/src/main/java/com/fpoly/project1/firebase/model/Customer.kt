package com.fpoly.project1.firebase.model

data class Customer(
    var id: String? = null,
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
