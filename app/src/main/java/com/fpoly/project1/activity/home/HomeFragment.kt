package com.fpoly.project1.activity.home

import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.fpoly.project1.R
import com.fpoly.project1.activity.MainActivity
import com.fpoly.project1.activity.home.adapter.FeaturedAdapter
import com.fpoly.project1.activity.home.adapter.MenuAdapter
import com.fpoly.project1.activity.product.ProductSearch
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot

class HomeFragment : Fragment(R.layout.home_main) {
    private val controllerCustomer = ControllerCustomer()
    private val controllerProduct = ControllerProduct()
    private lateinit var statusBarWelcomeView: TextView

    override fun onResume() {
        super.onResume()
        statusBarWelcomeView = requireActivity().findViewById(R.id.home_txt_statusBar_title)

        // greeting
        controllerCustomer.getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    dataSnapshot?.children?.forEach {
                        val account = it.getValue(Customer::class.java)!!
                        if (account.id == SessionUser.sessionId)
                            statusBarWelcomeView.text = "Hello, ${account.fullName}"
                    }
                }
            },
            null
        )

        // search box
        requireActivity().findViewById<EditText>(R.id.home_edt_search).setOnClickListener {
            requireActivity().let {
                it.findViewById<ViewPager>(R.id.viewPager)
                    .currentItem = 1
                it.findViewById<BottomNavigationView>(R.id.home_bottom_navigation)
                    .selectedItemId = R.id.mOrder
            }
        }

        // menu list - limit 10
        val menuView =
            requireActivity().findViewById<RecyclerView>(R.id.home_recyclerView_product_menu)
        controllerProduct.getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val products = ArrayList<Product>()
                    if (dataSnapshot != null)
                        for (child in dataSnapshot.children) {
                            products.add(child.getValue(Product::class.java)!!)
                        }

                    ControllerProductCategory().getAllAsync(
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run(dataSnapshot: DataSnapshot?) {
                                val categories = ArrayList<ProductCategory>()
                                if (dataSnapshot != null)
                                    for (child in dataSnapshot.children) {
                                        categories.add(child.getValue(ProductCategory::class.java)!!)
                                    }

                                menuView.adapter =
                                    MenuAdapter(requireContext(), products, categories)
                            }
                        },
                        null
                    )
                }
            },
            null
        )

        // TODO implement featured products by purchase amounts for past week (or a period of time)
        // featured list - limit 10
        val featuredView =
            requireActivity().findViewById<RecyclerView>(R.id.home_recyclerView_product_featured)
        controllerProduct.getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val list = ArrayList<Product>()
                    if (dataSnapshot != null)
                        for (child in dataSnapshot.children) {
                            list.add(child.getValue(Product::class.java)!!)
                        }

	                featuredView.adapter = FeaturedAdapter(requireContext(), list)
                }
            },
	        null
        )
    }
}
