package com.fpoly.project1.activity.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.fpoly.project1.R
import com.fpoly.project1.activity.MenuID
import com.fpoly.project1.activity.home.adapter.FeaturedAdapter
import com.fpoly.project1.activity.home.adapter.MenuAdapter
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot

class HomeFragment : Fragment(R.layout.home_main) {
    private val LIMIT_MENU = 5
    private val LIMIT_FEATURED = 20

    private val controllerCustomer = ControllerCustomer()
    private val controllerProduct = ControllerProduct()

    private lateinit var statusBar: TextView
    private lateinit var searchBox: EditText
    private lateinit var seeAllMenu: TextView
    private lateinit var seeAllFeatured: TextView
    private lateinit var recyclerMenu: RecyclerView
    private lateinit var recyclerFeatured: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_main, container, false)

        statusBar = view.findViewById(R.id.home_txt_statusBar_title)
        searchBox = view.findViewById(R.id.home_edt_search)
        seeAllMenu = view.findViewById(R.id.home_txt_seeAll_product_menu)
        seeAllFeatured = view.findViewById(R.id.home_txt_seeAll_product_featured)
        recyclerMenu = view.findViewById(R.id.home_recyclerView_product_menu)
        recyclerFeatured = view.findViewById(R.id.home_recyclerView_product_featured)

        // search box
        fun switchToSearchFragment() {
            requireActivity().findViewById<ViewPager>(R.id.viewPager)
                .currentItem = MenuID.Search
        }
        searchBox.setOnClickListener { switchToSearchFragment() }
        seeAllMenu.setOnClickListener { switchToSearchFragment() }
        seeAllFeatured.setOnClickListener { switchToSearchFragment() }

        return view
    }

    override fun onResume() {
        super.onResume()

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

        // menu list - limit 5
        Firebase.database.child(controllerProduct.table).limitToLast(LIMIT_MENU)
            .get().addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                val products = ArrayList<Product>()
                task.result?.children?.forEach { entry ->
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
                                MenuAdapter(
                                    requireContext(), products.reversed(),
                                    categories
                                )
                        }
                    },
                    null
                )
            }

        // featured list - limit 20
        Firebase.database.child(controllerProduct.table).limitToLast(LIMIT_FEATURED)
            .get().addOnCompleteListener { task ->
                val list = ArrayList<Product>()
                task.result?.children?.forEach { entry ->
                    list.add(entry.getValue(Product::class.java)!!)
                }

                recyclerFeatured.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                recyclerFeatured.adapter = FeaturedAdapter(
                    requireContext(), list.reversed()
                )
            }
    }
}
