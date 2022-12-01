package com.fpoly.project1.activity.product

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct

class ProductDetails : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_product_details)

        val customer = controllerCustomer.getSync(SessionUser.sessionId)

        val product = ControllerProduct().getSync(intent.extras?.getString("id", null))
        if (customer == null || product == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show()
            return
        }

        // bindings
        findViewById<ImageView>(R.id.details_iv_back).setOnClickListener { finish() }

        findViewById<ImageView>(R.id.details_iv_product).let {
            Glide.with(this).load(product.thumbnails?.get(0)).into(it)
        }
        findViewById<TextView>(R.id.details_txt_name_product).text = product.name
        findViewById<TextView>(R.id.details_txt_price).text = product.price
        findViewById<TextView>(R.id.details_txt_description).text = product.description
        findViewById<ImageView>(R.id.details_iv_favourite).setOnClickListener {
            val isFavorite: Boolean
            val currentFavoriteList = customer.favoriteIds
            val replicaFavoriteList = customer.favoriteIds?.toMutableList() ?: ArrayList()

            if (currentFavoriteList != null && currentFavoriteList.contains(product.__id)) {
                isFavorite = false
                replicaFavoriteList.remove(product.__id)
            } else {
                isFavorite = true
                replicaFavoriteList.add(product.__id)
            }

            customer.favoriteIds = replicaFavoriteList
            if (controllerCustomer.setSync(customer, true)) {
                Toast.makeText(this, "Updated favorite status", Toast.LENGTH_SHORT).show()

                // TODO untested color check
                it.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(if (isFavorite) R.color.accent else R.color.button_background))
                it.foregroundTintList =
                    ColorStateList.valueOf(resources.getColor(if (isFavorite) R.color.button_background else R.color.accent))
            } else {
                Toast.makeText(this, "Failed to edit favorite status", Toast.LENGTH_SHORT).show()
            }
        }

        val cartAmountView = findViewById<TextView>(R.id.details_txt_amount)
        findViewById<AppCompatButton>(R.id.details_btn_add).setOnClickListener {
            cartAmountView.text = (cartAmountView.text.toString().toInt() + 1).toString()
        }
        findViewById<AppCompatButton>(R.id.details_btn_minimize).setOnClickListener {
            val cartAmountNumber = cartAmountView.text.toString().toInt()

            cartAmountView.text =
                if (cartAmountNumber - 1 <= 0) "1" else (cartAmountNumber - 1).toString()
        }
        findViewById<Button>(R.id.details_btn_add_to_cart).setOnClickListener {
            SessionUser.cart.add(Pair(product.__id, cartAmountView.text.toString().toInt()))

            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
        }
    }
}