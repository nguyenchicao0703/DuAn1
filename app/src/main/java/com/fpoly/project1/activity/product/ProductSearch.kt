package com.fpoly.project1.activity.product

import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ProductSearch : Fragment(R.layout.product_search) {
    private val products = ArrayList<Product>()
    private val categories = ArrayList<ProductCategory>()

    private lateinit var productRecycler: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var searchBox: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_search, container, false)

        view.let {
            // bindings
            productRecycler = it.findViewById(R.id.search_recyclerView)
            productRecycler.adapter = ProductSearchAdapter(
                requireContext(),
                products,
                categories
            )

            backButton = it.findViewById(R.id.search_iv_back)
            backButton.setOnClickListener {
                if (parentFragmentManager.backStackEntryCount > 0)
                    parentFragmentManager.popBackStack()
            }

            searchBox = it.findViewById(R.id.search_edt_search)
            searchBox.let { et ->
                // disable until data is loaded
                et.isEnabled = false

                var searchTimer: Timer? = null

                et.addTextChangedListener(
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
                                            (productRecycler.adapter as ProductSearchAdapter).updateList(
                                                if (et.text.toString().isNotEmpty())
                                                    products.filter { product ->
                                                        product.name!!.contains(et.text.toString())
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
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        ControllerProduct().getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    dataSnapshot?.children?.let {
                        products.clear()
                        it.forEach {
                            products.add(it.getValue(Product::class.java)!!)
                        }
                    }

                    ControllerProductCategory().getAllAsync(
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run(dataSnapshot: DataSnapshot?) {
                                dataSnapshot?.children?.let {
                                    categories.clear()
                                    it.forEach {
                                        categories.add(it.getValue(ProductCategory::class.java)!!)
                                    }
                                }

                                productRecycler.adapter =
                                    ProductSearchAdapter(requireContext(), products, categories)

                                // enable once data is loaded
                                searchBox.isEnabled = true
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
