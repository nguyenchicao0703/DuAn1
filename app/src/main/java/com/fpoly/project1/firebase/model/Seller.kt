package com.fpoly.project1.firebase.model

class Seller {
    var __id: String? = null
    var name: String? = null
    var avatarUrl: String? = null
    var emailAddress: String? = null
    var phoneNumber: String? = null
    var postalAddress: String? = null
    var openingTime: String? = null
    var closingTime: String? = null
    var featuredImages: Array<String>? = null

    constructor()

    /**
     * @param __id           Firebase seller unique ID
     * @param name           Seller display name
     * @param avatarUrl      Seller display avatar url
     * @param emailAddress   Seller contact email address
     * @param phoneNumber    Seller contact phone number
     * @param postalAddress  Seller postal address
     * @param openingTime    Seller opening time
     * @param closingTime    Seller closing time
     * @param featuredImages Seller featured images as list of urls
     */
    constructor(
        __id: String?,
        name: String?,
        avatarUrl: String?,
        emailAddress: String?,
        phoneNumber: String?,
        postalAddress: String?,
        openingTime: String?,
        closingTime: String?,
        featuredImages: Array<String>
    ) {
        this.__id = __id
        this.name = name
        this.avatarUrl = avatarUrl
        this.emailAddress = emailAddress
        this.phoneNumber = phoneNumber
        this.postalAddress = postalAddress
        this.openingTime = openingTime
        this.closingTime = closingTime
        this.featuredImages = featuredImages
    }
}