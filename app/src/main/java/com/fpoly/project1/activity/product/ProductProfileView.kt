package com.fpoly.project1.activity.product

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatView
import com.fpoly.project1.activity.product.adapter.ProductProfileAdapter
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import de.hdodenhof.circleimageview.CircleImageView

class ProductProfileView : BottomSheetDialogFragment() {
    private lateinit var productAdapter: ProductProfileAdapter
    private var customer: Customer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_overview_other, container, false)

        ControllerCustomer().getAsync(arguments?.getString("id", null),
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    customer = dataSnapshot?.getValue(Customer::class.java)!!

                    runLater(view)
                }
            }, null
        )

        return view
    }

    private fun runLater(view: View) {
        if (customer == null) {
            Toast.makeText(requireContext(), "Unable to find user", Toast.LENGTH_SHORT).show()
            return
        }

        view.findViewById<ImageView>(R.id.other_users_iv_back).setOnClickListener { dismiss() }
        view.findViewById<CircleImageView>(R.id.other_users_iv_avt).let {
            Firebase.storage.child("/avatars/${customer!!.id}.jpg").downloadUrl
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        Glide.with(this).load(task.result).into(it)
                    else
                        Glide.with(this).load(customer!!.avatarUrl).into(it)
                }
        }
        view.findViewById<TextView>(R.id.other_users_txt_user_name).text = customer!!.fullName
        view.findViewById<TextView>(R.id.other_users_txt_user_email).visibility = View.GONE
        view.findViewById<RecyclerView>(R.id.other_users_recyclerView_product).let {
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

        view.findViewById<AppCompatButton>(R.id.other_users_btn_chat).setOnClickListener {
            val bundleData = Bundle()
            bundleData.putString("id", customer!!.id)

            val intentData = Intent(requireContext(), ChatView::class.java)
            intentData.putExtras(bundleData)

            startActivity(intentData)
        }
    }
}
