package com.fpoly.project1.firebase.model

class ProductCategory {
    var __id: String? = null
    var name: String? = null
    var description: String? = null
    var featuredThumbnail: String? = null

    constructor()

    /**
     * @param __id              Firebase Rating unique ID
     * @param name              Rating display name
     * @param description       Rating description
     * @param featuredThumbnail Rating featured thumbnail TODO: should use product ID or not
     */
    constructor(__id: String?, name: String?, description: String?, featuredThumbnail: String?) {
        this.__id = __id
        this.name = name
        this.description = description
        this.featuredThumbnail = featuredThumbnail
    }
}