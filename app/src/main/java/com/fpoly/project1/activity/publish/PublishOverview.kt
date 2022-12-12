package com.fpoly.project1.activity.publish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.publish.adapter.PublishOverviewAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot

class PublishOverview : Fragment(R.layout.sell_overview) {
    private lateinit var searchBox: EditText
    private lateinit var btnNew: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: PublishOverviewAdapter

    private val categories = ArrayList<ProductCategory>()
    private val products = ArrayList<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sell_overview, container, false)

        view.let {
            searchBox = it.findViewById(R.id.sell_edt_sell)
            recyclerView = it.findViewById(R.id.sell_recyclerView)
            btnNew = it.findViewById(R.id.sell_btn_add)

            recyclerViewAdapter =
                PublishOverviewAdapter(
                    requireContext(),
                    products,
                    categories
                )
            recyclerView.adapter = recyclerViewAdapter

            searchBox.doOnTextChanged { text, _, _, _ ->
                recyclerViewAdapter.updateList(
                    newProductList = (if (text != null)
                        products.filter { entry ->
                            entry.name!!.contains(text)
                        } as ArrayList<Product>
                    else products),
                    newCategoryList = null
                )
            }

            btnNew.setOnClickListener {
                val fragment = PublishAddItem()
                fragment.show(parentFragmentManager, "publish_add_item")
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        // run last
        fun getProducts() {
            ControllerProduct().getAllAsync(
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        dataSnapshot?.children?.let {
                            products.clear()
                            it.forEach { entry ->
                                entry.getValue(Product::class.java)?.let { product ->
                                    if (product.sellerId == SessionUser.sessionId)
                                        products.add(product)
                                }
                            }
                        }

                        recyclerViewAdapter = PublishOverviewAdapter(
                            requireContext(),
                            products,
                            categories
                        )
                        recyclerView.adapter = recyclerViewAdapter
                    }
                },
                failureListener = null
            )
        }

        // run first
        fun getCategories() {
            ControllerProductCategory().getAllAsync(
                object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        dataSnapshot?.children?.let {
                            categories.clear()
                            it.forEach { entry ->
                                categories.add(entry.getValue(ProductCategory::class.java)!!)
                            }
                        }

                        getProducts()
                    }
                },
                failureListener = null
            )
        }

        // run first
        getCategories()
    }
}