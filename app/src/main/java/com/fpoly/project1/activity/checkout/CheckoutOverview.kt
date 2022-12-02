package com.fpoly.project1.activity.checkout

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Product

class CheckoutOverview : AppCompatActivity() {
	private val controllerProduct = ControllerProduct()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.checkout_cart)

		val cartProducts = ArrayList<Product>()
		SessionUser.cart.forEach { cartProducts.add(controllerProduct.getSync(it.first)!!) }

		findViewById<ImageView>(R.id.cart_iv_back).setOnClickListener { finish() }
		findViewById<RecyclerView>(R.id.cart_recyclerView_product).let {
			it.adapter
		}

	}
}