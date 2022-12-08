package com.fpoly.project1.activity.product

import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.adapter.ProductSearchAdapter
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot
import java.util.*
import java.util.logging.Handler

class ProductSearch : Fragment(R.layout.product_search) {
    private var products = ArrayList<Product>()
    private var categories = ArrayList<ProductCategory>()
    private var productRecycler: RecyclerView? = null

    override fun onResume() {
        super.onResume()

        // bindings
        productRecycler =
            productRecycler ?: requireActivity().findViewById(R.id.search_recyclerView)
        productRecycler!!.adapter = ProductSearchAdapter(requireActivity(), products, categories)

        requireActivity().findViewById<ImageView>(R.id.search_iv_back).setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0)
                parentFragmentManager.popBackStack()
        }
        requireActivity().findViewById<EditText>(R.id.search_edt_search).let {
            var searchTimer: Timer? = null

            it.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        if (searchTimer != null) {
                            searchTimer!!.cancel()
                            searchTimer = null
                        }
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        // ignored
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val searchDelay = 1500L

                        searchTimer = Timer()
                        searchTimer!!.schedule(
                            object : TimerTask() {
                                override fun run() {
                                    // run in main thread
                                    android.os.Handler(Looper.getMainLooper()).post {
                                        (productRecycler!!.adapter as ProductSearchAdapter).updateList(
                                            if (it.text.toString().isNotEmpty())
                                                products.filter { product ->
                                                    product.name!!.contains(it.text.toString())
                                                }
                                            else products,
                                            null
                                        )
                                    }
                                }
                            },
                            searchDelay
                        )
                    }
                }
            )
        }

        if (products.size < 1 && categories.size < 1) {
            ControllerProduct().getAllAsync(
                successListener = object : ControllerBase.SuccessListener() {
                    override fun run(dataSnapshot: DataSnapshot?) {
                        dataSnapshot?.children?.forEach {
                            products.add(it.getValue(Product::class.java)!!)
                        }

                        println("Added ${products.size} p")

                        ControllerProductCategory().getAllAsync(
                            successListener = object : ControllerBase.SuccessListener() {
                                override fun run(dataSnapshot: DataSnapshot?) {
                                    dataSnapshot?.children?.forEach {
                                        categories.add(it.getValue(ProductCategory::class.java)!!)
                                    }

                                    println("Added ${categories.size} c")

                                    (productRecycler!!.adapter as ProductSearchAdapter)
                                        .updateList(products, categories)
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
