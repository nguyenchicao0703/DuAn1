package com.fpoly.project1.activity.product

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatView
import com.fpoly.project1.activity.product.adapter.ProductProfileAdapter
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot

class ProductProfileView : BottomSheetDialogFragment() {
    private var customer: Customer? = null

    private lateinit var productAdapter: ProductProfileAdapter
    private lateinit var backButton: ImageView
    private lateinit var userAvatar: ImageView
    private lateinit var userFullName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userProducts: RecyclerView
    private lateinit var userChat: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_overview_other, container, false)

        backButton = view.findViewById(R.id.other_users_iv_back)
        userAvatar = view.findViewById(R.id.other_users_iv_avt)
        userFullName = view.findViewById(R.id.other_users_txt_user_name)
        userEmail = view.findViewById(R.id.other_users_txt_user_email)
        userProducts = view.findViewById(R.id.other_users_recyclerView_product)
        userChat = view.findViewById(R.id.other_users_btn_chat)

        ControllerCustomer().getAsync(
            arguments?.getString("id", null),
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    customer = dataSnapshot?.getValue(Customer::class.java)!!

                    runLater()
                }
            }, null
        )

        return view
    }

    private fun runLater() {
        if (customer == null) {
            Toast.makeText(requireContext(), "Unable to find user", Toast.LENGTH_SHORT).show()
            return
        }

        backButton.setOnClickListener { dismiss() }

        userFullName.text = customer!!.fullName
        userEmail.visibility = View.GONE
        customer!!.getAvatarUrl(object : ControllerBase.SuccessListener() {
            override fun run(unused: Any?) {
                Glide.with(this@ProductProfileView).load(unused as String).into(userAvatar)
            }
        })
        userChat.setOnClickListener {
            val intentData = Intent(requireContext(), ChatView::class.java)
            intentData.putExtras(bundleOf(Pair("id", customer!!.id)))

            startActivity(intentData)
        }
        userProducts.let {
            ControllerProduct().getAllAsync(
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        val products = ArrayList<Product>()
                        dataSnapshot?.children?.forEach { entry ->
                            products.add(entry.getValue(Product::class.java)!!)
                        }

                        ControllerProductCategory().getAllAsync(
                            successListener = object : ControllerBase.SuccessListener() {
                                override fun run(dataSnapshot: DataSnapshot?) {
                                    val categories = ArrayList<ProductCategory>()
                                    dataSnapshot?.children?.forEach { entry ->
                                        categories.add(entry.getValue(ProductCategory::class.java)!!)
                                    }

                                    productAdapter = ProductProfileAdapter(
                                        requireContext(),
                                        products, categories
                                    )
                                    it.adapter = productAdapter
                                }
                            },
                            null
                        )
                    }
                },
                null
            )
        }
    }
}
