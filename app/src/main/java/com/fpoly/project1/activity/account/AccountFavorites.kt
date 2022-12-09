package com.fpoly.project1.activity.account

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.adapter.FavoriteProductsAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot

class AccountFavorites : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()
    private lateinit var favoriteProductsAdapter: FavoriteProductsAdapter

    private lateinit var searchBox: ConstraintLayout
    private lateinit var backButton: ImageView
    private lateinit var favoriteRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_favorite_prods)

        favoriteRecyclerView = findViewById(R.id.favorite_recycler_favorite)

        searchBox = findViewById(R.id.favorite_constraint_search)
        searchBox.visibility = View.GONE

        backButton = findViewById(R.id.favorite_product_iv_back)
        backButton.setOnClickListener { finish() }

        controllerCustomer.getAsync(
            SessionUser.sessionId,
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val customer = dataSnapshot?.getValue(Customer::class.java)!!

                    ControllerProductCategory().getAllAsync(
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run(dataSnapshot: DataSnapshot?) {
                                val categories = ArrayList<ProductCategory>()
                                dataSnapshot?.children?.forEach {
                                    categories.add(it.getValue(ProductCategory::class.java)!!)
                                }

                                if (customer.favoriteIds != null) {
                                    favoriteProductsAdapter = FavoriteProductsAdapter(
                                        this@AccountFavorites,
                                        customer,
                                        customer.favoriteIds!!.toMutableList(),
                                        categories
                                    )
                                    favoriteRecyclerView.adapter = favoriteProductsAdapter
                                }
                            }
                        },
	                    failureListener
                    )
                }
            },
	        failureListener
        )
    }

    private val failureListener = object : ControllerBase.FailureListener() {
        override fun run(error: Exception?) {
            Toast.makeText(
                this@AccountFavorites, "Failed to retrieve data from server", Toast
                    .LENGTH_SHORT
            ).show()
        }
    }
}
