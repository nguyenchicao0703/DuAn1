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
import java.util.*

class AccountFavorites : AppCompatActivity() {
	private var searchTimer: Timer? = null
	private val productList: MutableList<Product> = ArrayList()
	private val controllerCustomer = ControllerCustomer()
	private val controllerProduct = ControllerProduct()
	private var favoriteSearchBox: EditText? = null
	private var favoriteProductsAdapter: FavoriteProductsAdapter? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.account_favorite_prods)

		val customer = controllerCustomer.getSync(SessionUser.sessionId)
		customer?.favoriteIds?.forEach { productId: String? ->
			productList.add(controllerProduct.getSync(productId)!!)
		}

		favoriteSearchBox = findViewById(R.id.favorite_edt_search)
		favoriteSearchBox!!.let {
			it.addTextChangedListener(
				object : TextWatcher {
					override fun beforeTextChanged(
						s: CharSequence?,
						start: Int,
						count: Int,
						after: Int
					) {
						if (searchTimer != null) searchTimer = null
					}

					override fun onTextChanged(
						s: CharSequence?,
						start: Int,
						before: Int,
						count: Int
					) {
						TODO("Ignored")
					}

					override fun afterTextChanged(s: Editable?) {
						searchTimer = Timer()
						searchTimer!!.schedule(
							object : TimerTask() {
								override fun run() {
									favoriteProductsAdapter!!.updateList(
										if (it.text.toString().isNotEmpty())
											productList.filter { product ->
												product.name!!.contains(it.text.toString())
											}
										else productList
									)
								}
							},
							1_500L
						)
					}
				}
			)
		}

        val favoriteRecycler = findViewById<RecyclerView>(R.id.favorite_recycler_favorite)
        favoriteProductsAdapter = FavoriteProductsAdapter(
            this,
            productList,
            controllerCustomer.getSync(SessionUser.sessionId)!!
        )
        favoriteRecycler.adapter = favoriteProductsAdapter
    }
}
