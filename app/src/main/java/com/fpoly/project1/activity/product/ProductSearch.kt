package com.fpoly.project1.activity.product

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.adapter.ProductSearchAdapter
import com.fpoly.project1.firebase.controller.ControllerProduct
import java.util.*

class ProductSearch : AppCompatActivity() {
	private val products = ControllerProduct().getAllSync()!!
	private lateinit var productRecycler: RecyclerView
	private lateinit var productSearchAdapter: ProductSearchAdapter

	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onCreate(savedInstanceState, persistentState)
		setContentView(R.layout.layout_search)

		// bindings
		productSearchAdapter = ProductSearchAdapter(this, products)
		productRecycler = findViewById(R.id.search_recyclerView)
		productRecycler.adapter = productSearchAdapter

		findViewById<ImageView>(R.id.search_iv_back).setOnClickListener { finish() }
		findViewById<EditText>(R.id.search_edt_search).let {
			var searchTimer: Timer? = null

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
						searchTimer!!.schedule(object : TimerTask() {
							override fun run() {
								productSearchAdapter.updateList(
									if (it.text.toString().isNotEmpty())
										products.filter { product ->
											product.name!!.contains(it.text.toString())
										}
									else products
								)
							}
						}, 1_500L)
					}

				}
			)
		}
	}
}