package com.fpoly.project1.activity.product

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatView
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import de.hdodenhof.circleimageview.CircleImageView

class ProductDetails : AppCompatActivity() {
    private val controllerCustomer = ControllerCustomer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_product_detailed)

        val customer = controllerCustomer.getSync(SessionUser.sessionId)
        val product = ControllerProduct().getSync(intent.extras?.getString("id", null))
        val owner = ControllerCustomer().getSync(product?.sellerId)
        if (customer == null || product == null || owner == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show()
            return
        }

        // bindings
        findViewById<ImageView>(R.id.userPage_iv_back).setOnClickListener { finish() }
        findViewById<CircleImageView>(R.id.userPage_iv_avata).let {
            Glide.with(this).load(owner.avatarUrl).into(it)
        }
        findViewById<TextView>(R.id.userPage_txt_user_name).text = owner.fullName
        // no we don't display user's email without their consent
        findViewById<TextView>(R.id.userPage_txt_user_email).visibility = View.GONE

        findViewById<ImageView>(R.id.userPage_iv_product).let {
            Glide.with(this).load(product.thumbnails?.get(0)).into(it)
        }
        findViewById<TextView>(R.id.userPage_txt_name_product).text = product.name
        findViewById<TextView>(R.id.userPage_txt_price).text = product.price
        findViewById<TextView>(R.id.userPage_txt_description).text = product.description
        findViewById<ImageView>(R.id.userPage_iv_favourite).setOnClickListener {
            val isFavorite: Boolean
            val currentFavoriteList = customer.favoriteIds
            val replicaFavoriteList = customer.favoriteIds?.toMutableList() ?: ArrayList()

            if (currentFavoriteList != null && currentFavoriteList.contains(product.id!!)) {
                isFavorite = false
                replicaFavoriteList.remove(product.id!!)
            } else {
                isFavorite = true
                replicaFavoriteList.add(product.id!!)
            }

            customer.favoriteIds = replicaFavoriteList
            if (controllerCustomer.setSync(customer, true)) {
                Toast.makeText(this, "Updated favorite status", Toast.LENGTH_SHORT).show()

                // TODO untested color check
                it.backgroundTintList =
                    ColorStateList.valueOf(
                        resources.getColor(
                            if (isFavorite) R.color.accent else R.color.button_background
                        )
                    )
                it.foregroundTintList =
                    ColorStateList.valueOf(
                        resources.getColor(
                            if (isFavorite) R.color.button_background else R.color.accent
                        )
                    )
            } else {
                Toast.makeText(this, "Failed to edit favorite status", Toast.LENGTH_SHORT).show()
            }
        }

        val cartAmountView = findViewById<TextView>(R.id.userPage_txt_amount)
        findViewById<AppCompatButton>(R.id.userPage_btn_add).setOnClickListener {
            cartAmountView.text = (cartAmountView.text.toString().toInt() + 1).toString()
        }
        findViewById<AppCompatButton>(R.id.userPage_btn_minimize).setOnClickListener {
            val cartAmountNumber = cartAmountView.text.toString().toInt()

            cartAmountView.text =
                if (cartAmountNumber - 1 <= 0) "1" else (cartAmountNumber - 1).toString()
        }
        findViewById<Button>(R.id.userPage_users_btn_cart).setOnClickListener {
            SessionUser.cart.add(Pair(product.id!!, cartAmountView.text.toString().toInt()))

            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
        }

        findViewById<AppCompatButton>(R.id.userPage_users_btn_chat).setOnClickListener {
            val bundleData = Bundle()
            bundleData.putString("id", owner.id)

            val intentData = Intent(this, ChatView::class.java)
            intentData.putExtras(bundleData)

            startActivity(intentData)
        }
    }
}
