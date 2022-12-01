package com.fpoly.project1.firebase.model

class Rating {
    var __id: String? = null
    var customerId: String? = null
    var sellerId: String? = null
    var shipperId: String? = null
    var productId: String? = null
    var comment: String? = null
    var date: String? = null
    var rating = 0f

    constructor()

    /**
     * @param __id       Firebase comment unique Id
     * @param customerId Comment customer owner ID
     * @param sellerId   Comment target seller ID, if any
     * @param shipperId  Comment target shipper ID, if any
     * @param productId  Comment target product ID, if any
     * @param comment    Comment text
     * @param date       Comment date
     * @param rating     Comment rating points 1.00 - 5.00
     */
    constructor(
        __id: String?,
        customerId: String?,
        sellerId: String?,
        shipperId: String?,
        productId: String?,
        comment: String?,
        date: String?,
        rating: Float
    ) {
        this.__id = __id
        this.customerId = customerId
        this.sellerId = sellerId
        this.shipperId = shipperId
        this.productId = productId
        this.comment = comment
        this.date = date
        this.rating = rating
    }
}