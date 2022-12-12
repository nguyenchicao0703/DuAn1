package com.fpoly.project1.activity.product

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatView
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import java.text.NumberFormat
import java.util.Locale

class ProductDetails : BottomSheetDialogFragment() {
    private val controllerCustomer = ControllerCustomer()
    private lateinit var product: Product
    private lateinit var owner: Customer
    private lateinit var customer: Customer

    private lateinit var backButton: ImageView
    private lateinit var ownerAvatar: ImageView
    private lateinit var ownerFullName: TextView
    private lateinit var ownerEmail: TextView
    private lateinit var ownerChat: Button

    private lateinit var productThumbnail: ImageView
    private lateinit var productName: TextView
    private lateinit var productDescription: TextView
    private lateinit var productPrice: TextView
    private lateinit var productFavoriteStatus: ImageView

    private lateinit var cartAmount: TextView
    private lateinit var cartRemove: Button
    private lateinit var cartAdd: Button
    private lateinit var cartButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_product_detailed, container, false)

        bindViews(view)

        // run last
        fun getOwner() {
            ControllerCustomer().getAsync(
                product.sellerId,
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        owner = dataSnapshot?.getValue(Customer::class.java)!!

                        runLater()
                    }
                }, null
            )
        }

        // run second
        fun getProduct() {
            ControllerProduct().getAsync(
                arguments?.getString("id", null),
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        product = dataSnapshot?.getValue(Product::class.java)!!

                        getOwner()
                    }
                }, null
            )
        }

        // run first
        fun getCustomer() {
            ControllerCustomer().getAsync(
                SessionUser.sessionId,
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        customer = dataSnapshot?.getValue(Customer::class.java)!!

                        getProduct()
                    }
                }, null
            )
        }

        // run async tasks
        getCustomer()

        return view
    }

    private fun bindViews(view: View) {
        backButton = view.findViewById(R.id.userPage_iv_back)
        ownerAvatar = view.findViewById(R.id.userPage_iv_avatar)
        ownerFullName = view.findViewById(R.id.userPage_txt_user_name)
        ownerEmail = view.findViewById(R.id.userPage_txt_user_email)
        ownerChat = view.findViewById(R.id.userPage_users_btn_chat)

        productThumbnail = view.findViewById(R.id.userPage_iv_product)
        productName = view.findViewById(R.id.userPage_txt_name_product)
        productDescription = view.findViewById(R.id.userPage_txt_description)
        productPrice = view.findViewById(R.id.userPage_txt_price)
        productFavoriteStatus = view.findViewById(R.id.userPage_iv_favourite)

        cartAmount = view.findViewById(R.id.userPage_txt_amount)
        cartAdd = view.findViewById(R.id.userPage_btn_add)
        cartRemove = view.findViewById(R.id.userPage_btn_minimize)
        cartButton = view.findViewById(R.id.userPage_users_btn_cart)
    }

    private fun runLater() {
        // bindings
        backButton.setOnClickListener { dismiss() }
        ownerAvatar.let {
            it.setOnClickListener { showProfile() }
            owner.getAvatarUrl(object : ControllerBase.SuccessListener() {
                override fun run(unused: Any?) {
                    Glide.with(this@ProductDetails).load(unused as String).into(ownerAvatar)
                }
            })
        }
        ownerFullName.let {
            it.text = owner.fullName
            it.setOnClickListener { showProfile() }
        }
        // no we don't display user's email without their consent
        ownerEmail.text = "email hidden for privacy"

        // chat button
        ownerChat.setOnClickListener {
            val intentData = Intent(requireContext(), ChatView::class.java)
            intentData.putExtras(bundleOf(Pair("id", owner.id)))

            startActivity(intentData)
        }

        // favorite status listener
        favoriteViewListener(productFavoriteStatus)

        Glide.with(this).load(product.thumbnails?.get(0)).into(productThumbnail)
        productName.text = product.name
        productPrice.text = NumberFormat.getIntegerInstance().format(product.price)
        productDescription.text = product.description

        // add amount to current view
        cartAdd.setOnClickListener {
            cartAmount.text = (cartAmount.text.toString().toInt() + 1).toString()
        }

        // remove amount from current view
        cartRemove.setOnClickListener {
            val cartAmountNumber = cartAmount.text.toString().toInt()

            cartAmount.text =
                if (cartAmountNumber - 1 <= 0) "1" else (cartAmountNumber - 1).toString()
        }

        // add to card
        cartButton.setOnClickListener {
            val existingCartItem = SessionUser.cart.find { pair -> pair.first.id == product.id }

            if (existingCartItem != null) {
                SessionUser.cart[SessionUser.cart.indexOf(existingCartItem)] =
                    Pair(product, existingCartItem.second + cartAmount.text.toString().toInt())
            } else {
                SessionUser.cart.add(Pair(product, cartAmount.text.toString().toInt()))
            }

            Toast.makeText(
                requireContext(), "Added ${cartAmount.text.toString().toInt()}x to " +
                        "cart", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun favoriteViewListener(favView: ImageView) {
        if (customer.favoriteIds?.any { id -> id == product.id } == true) {
            favView.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.accent))
            favView.foregroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.button_background))
        }

        favView.setOnClickListener {
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
            controllerCustomer.setAsync(customer, true,
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run() {
                        Toast.makeText(
                            requireContext(),
                            "Updated favorite status",
                            Toast.LENGTH_SHORT
                        ).show()

                        // TODO untested color check
                        favView.backgroundTintList =
                            ColorStateList.valueOf(
                                resources.getColor(
                                    if (isFavorite) R.color.accent else R.color.button_background
                                )
                            )
                        favView.foregroundTintList =
                            ColorStateList.valueOf(
                                resources.getColor(
                                    if (isFavorite) R.color.button_background else R.color.accent
                                )
                            )
                    }
                },
                failureListener = object : ControllerBase.FailureListener() {
                    override fun run(error: Exception?) {
                        Toast.makeText(
                            requireContext(), "Failed to add to favorites", Toast
                                .LENGTH_SHORT
                        ).show()

                        Log.e(this@ProductDetails::class.simpleName, "Error", error)
                    }
                })
        }
    }

    private fun showProfile() {
        val fragment = ProductProfileView()
        fragment.arguments = bundleOf(Pair("id", owner.id!!))
        fragment.show(parentFragmentManager, "product_profile_view")
    }
}
