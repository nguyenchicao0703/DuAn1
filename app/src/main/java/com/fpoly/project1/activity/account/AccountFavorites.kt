package com.fpoly.project1.activity.account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.adapter.FavoriteProductsAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot
import java.util.*
import kotlin.collections.ArrayList

class AccountFavorites : AppCompatActivity() {
	private val controllerCustomer = ControllerCustomer()
	private var favoriteProductsAdapter: FavoriteProductsAdapter? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.account_favorite_prods)

		findViewById<ConstraintLayout>(R.id.favorite_constraint_search).visibility = View.GONE
		findViewById<ImageView>(R.id.favorite_product_iv_back).setOnClickListener { finish() }

		controllerCustomer.getAsync(SessionUser.sessionId,
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
								val favoriteRecycler =
									findViewById<RecyclerView>(R.id.favorite_recycler_favorite)
								favoriteProductsAdapter = FavoriteProductsAdapter(
									this@AccountFavorites,
									customer,
									customer.favoriteIds!!.toMutableList(),
									categories
								)
								favoriteRecycler.adapter = favoriteProductsAdapter
							}
						}
					},
					failureListener = null
				)
			}
		},
		failureListener = null)
    }
}
