package com.fpoly.project1.activity.home

import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.fpoly.project1.R
import com.fpoly.project1.activity.home.adapter.FeaturedAdapter
import com.fpoly.project1.activity.home.adapter.MenuAdapter
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

    private lateinit var statusBar: TextView
    private lateinit var searchBox: EditText
    private lateinit var seeAllMenu: TextView
    private lateinit var seeAllFeatured: TextView
    private lateinit var recyclerMenu: RecyclerView
    private lateinit var recyclerFeatured: RecyclerView

    override fun onResume() {
        super.onResume()

        statusBar = requireActivity().findViewById(R.id.home_txt_statusBar_title)
        searchBox = requireActivity().findViewById(R.id.home_edt_search)
        seeAllMenu = requireActivity().findViewById(R.id.home_txt_seeAll_product_menu)
        seeAllFeatured = requireActivity().findViewById(R.id.home_txt_seeAll_product_featured)
        recyclerMenu = requireActivity().findViewById(R.id.home_recyclerView_product_menu)
        recyclerFeatured = requireActivity().findViewById(R.id.home_recyclerView_product_featured)

        // search box
        fun switchToSearchFragment() {
            requireActivity().let {
                it.findViewById<ViewPager>(R.id.viewPager)
                    .currentItem = 1
                it.findViewById<BottomNavigationView>(R.id.home_bottom_navigation)
                    .selectedItemId = R.id.mSearch
            }
        }
        searchBox.setOnClickListener { switchToSearchFragment() }
        seeAllMenu.setOnClickListener { switchToSearchFragment() }
        seeAllFeatured.setOnClickListener { switchToSearchFragment() }

        // greeting
        controllerCustomer.getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    dataSnapshot?.children?.forEach {
                        val account = it.getValue(Customer::class.java)!!
                        if (account.id == SessionUser.sessionId)
                            statusBar.text = "Hello, ${account.fullName}"
                    }
                }
            },
            failureListener = null
        )

        // menu list - limit 10
        controllerProduct.getAllAsync(
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

                                recyclerMenu.adapter =
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
        controllerProduct.getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val list = ArrayList<Product>()
                    dataSnapshot?.children?.forEach { entry ->
                        list.add(entry.getValue(Product::class.java)!!)
                    }

                    recyclerFeatured.adapter = FeaturedAdapter(requireContext(), list)
                }
            },
            null
        )
    }
}
