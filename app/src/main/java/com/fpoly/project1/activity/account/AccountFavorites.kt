package com.fpoly.project1.activity.account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.adapter.FavoriteProductsAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Product

class AccountFavorites : AppCompatActivity() {
    private val productList: MutableList<Product> = ArrayList()
    private val controllerCustomer = ControllerCustomer()
    private val controllerProduct = ControllerProduct()
    private var favoriteSearchBox: EditText? = null
    private var favoriteProductsAdapter: FavoriteProductsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_product)

        val customer = controllerCustomer.getSync(SessionUser.sessionId)
        customer?.favoriteIds?.forEach { productId: String? ->
            productList.add(controllerProduct.getSync(productId))
        }

        favoriteSearchBox = findViewById(R.id.favorite_edt_search)
        favoriteSearchBox!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignored
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignored
            }

            override fun afterTextChanged(s: Editable) {
                // get search query
                val searchQuery = favoriteSearchBox!!.text.toString()

                // if not empty
                if (searchQuery.isNotEmpty()) {
                    val matchingProductList = productList.filter { product: Product ->
                        product.name.equals(
                            searchQuery,
                            ignoreCase = true
                        )
                    }
                    favoriteProductsAdapter!!.updateList(matchingProductList)
                } else {
                    // set back to normal
                    favoriteProductsAdapter!!.updateList(productList)
                }
            }
        })

        val favoriteRecycler = findViewById<RecyclerView>(R.id.favorite_recycler_favorite)
        favoriteProductsAdapter = FavoriteProductsAdapter(
            this,
            productList,
            controllerCustomer.getSync(SessionUser.sessionId)!!
        )
        favoriteRecycler.adapter = favoriteProductsAdapter
    }
}