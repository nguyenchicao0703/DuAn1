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
import com.fpoly.project1.firebase.controller.*
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Order
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot

class HomeFragment : Fragment(R.layout.home_main) {
    // recently added limit
    private val LIMIT_MENU = 20

    // featured limit
    private val LIMIT_FEATURED = 20

    // featured orders limit to look back
    private val LIMIT_FEATURED_ORDERS = 50

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

    override fun onStart() {
        super.onStart()

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

        // menu list
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

        // featured list
        Firebase.database.child(ControllerOrder().table)
            .limitToLast(LIMIT_FEATURED_ORDERS)
            .get().addOnCompleteListener { orderTask ->
                if (orderTask.isSuccessful) {
                    val productMap = HashMap<String, Long>()

                    orderTask.result?.children?.forEach { order ->
                        order.getValue(Order::class.java)?.list?.forEach { pair ->
                            productMap[pair.key] = productMap.getOrDefault(pair.key, 0) + pair.value
                        }
                    }

                    recyclerFeatured.layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                    recyclerFeatured.adapter = FeaturedAdapter(
                        requireContext(),
                        productMap.toList()
                            .sortedBy { value -> value.second }
                            .asReversed()
                            .take(LIMIT_FEATURED)
                    )
                }
            }
    }
}
