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
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatView
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import de.hdodenhof.circleimageview.CircleImageView

class ProductDetails : BottomSheetDialogFragment() {
    private val controllerCustomer = ControllerCustomer()
    private lateinit var product: Product
    private lateinit var owner: Customer
    private lateinit var customer: Customer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_product_detailed, container, false)

        ControllerCustomer().getAsync(
            SessionUser.sessionId,
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    customer = dataSnapshot?.getValue(Customer::class.java)!!

                    ControllerProduct().getAsync(
                        arguments?.getString("id", null),
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run(dataSnapshot: DataSnapshot?) {
                                product = dataSnapshot?.getValue(Product::class.java)!!

                                ControllerCustomer().getAsync(
                                    product.sellerId,
                                    successListener = object : ControllerBase.SuccessListener() {
                                        override fun run(dataSnapshot: DataSnapshot?) {
                                            owner = dataSnapshot?.getValue(Customer::class.java)!!

                                            runLater(view)
                                        }
                                    }, null
                                )
                            }
                        }, null
                    )
                }
            }, null
        )

        return view
    }

    private fun runLater(view: View) {
        // bindings
        view.findViewById<ImageView>(R.id.userPage_iv_back).setOnClickListener {
            this.dismiss()
        }
        view.findViewById<CircleImageView>(R.id.userPage_iv_avatar).let {
            it.setOnClickListener { showProfile() }
            Firebase.storage.child("/avatars/${owner.id}.jpg").downloadUrl
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        Glide.with(this).load(task.result).into(it)
                    else
                        Glide.with(this).load(owner.avatarUrl).into(it)
                }
        }
        view.findViewById<TextView>(R.id.userPage_txt_user_name).let {
            it.text = owner.fullName
            it.setOnClickListener { showProfile() }
        }
        // no we don't display user's email without their consent
        view.findViewById<TextView>(R.id.userPage_txt_user_email).let {
            it.text = "email hidden for privacy"
        }

        view.findViewById<ImageView>(R.id.userPage_iv_product).let {
            Glide.with(this).load(product.thumbnails?.get(0)).into(it)
        }
        view.findViewById<TextView>(R.id.userPage_txt_name_product).text = product.name
        view.findViewById<TextView>(R.id.userPage_txt_price).text =
            product.price.toString()
        view.findViewById<TextView>(R.id.userPage_txt_description).text =
            product.description

        favoriteViewListener(view.findViewById<ImageView>(R.id.userPage_iv_favourite))

        val cartAmountView = view.findViewById<TextView>(R.id.userPage_txt_amount)
        view.findViewById<AppCompatButton>(R.id.userPage_btn_add).setOnClickListener {
            cartAmountView.text = (cartAmountView.text.toString().toInt() + 1).toString()
        }
        view.findViewById<AppCompatButton>(R.id.userPage_btn_minimize)
            .setOnClickListener {
                val cartAmountNumber = cartAmountView.text.toString().toInt()

                cartAmountView.text =
                    if (cartAmountNumber - 1 <= 0) "1" else (cartAmountNumber - 1).toString()
            }
        view.findViewById<Button>(R.id.userPage_users_btn_cart).setOnClickListener {
            SessionUser.cart.add(Pair(product, cartAmountView.text.toString().toInt()))

            Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<AppCompatButton>(R.id.userPage_users_btn_chat)
            .setOnClickListener {
                val bundleData = Bundle()
                bundleData.putString("id", owner.id)

                val intentData = Intent(requireContext(), ChatView::class.java)
                intentData.putExtras(bundleData)

                startActivity(intentData)
            }
    }

    private fun favoriteViewListener(favView: ImageView) {
        if (customer.favoriteIds?.any { id -> id == product.id } == true) {
            favView.backgroundTintList =
                ColorStateList.valueOf(
                    resources.getColor(R.color.accent)
                )
            favView.foregroundTintList =
                ColorStateList.valueOf(
                    resources.getColor(R.color.button_background)
                )
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
                        )
                            .show()

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
        val bundle = Bundle()
        bundle.putString("id", owner.id!!)

        val fragment = ProductProfileView()
        fragment.arguments = bundle
        fragment.show(parentFragmentManager, "product_profile_view")
    }
}
