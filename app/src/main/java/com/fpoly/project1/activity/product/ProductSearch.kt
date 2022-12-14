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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.adapter.ProductSearchAdapter
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot
import java.util.*
import kotlin.math.abs

class ProductSearch : Fragment(R.layout.product_search) {
    private val pageCount = 20
    private val pageScrollDiff = 50
    private val pageScrollDelay = 1500L
    private var pageScrollTimer: Timer? = null

    private val searchDelay = 500L
    private fun searchTask() {
        android.os.Handler(Looper.getMainLooper()).post {
            if (searchBox.text.toString().isNotEmpty()) {
                ControllerProduct().getAllAsync(
                    successListener = object : ControllerBase.SuccessListener() {
                        override fun run(dataSnapshot: DataSnapshot?) {
                            val arrayList = ArrayList<Product>()
                            dataSnapshot?.children?.forEach { entry ->
                                arrayList.add(entry.getValue(Product::class.java)!!)
                            }

                            (productRecycler.adapter as ProductSearchAdapter).updateList(
                                arrayList.filter { product ->
                                    product.name!!.contains(searchBox.text.toString())
                                },
                                null
                            )
                        }
                    },
                    failureListener = null
                )
            } else {
                (productRecycler.adapter as ProductSearchAdapter).updateList(
                    products,
                    null
                )
            }
        }
    }

    private val products = ArrayList<Product>()
    private val categories = ArrayList<ProductCategory>()

    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var productRecycler: RecyclerView
    private lateinit var searchBox: EditText

    private lateinit var loadingIv: ImageView
    private lateinit var loadingLL: LinearLayout
    private lateinit var loadingTv: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_search, container, false)

        // bindings
        nestedScrollView = view.findViewById(R.id.search_scrollview)
        nestedScrollView.setOnScrollChangeListener(onScrollListener)

        productRecycler = view.findViewById(R.id.search_recyclerView)
        productRecycler.adapter = ProductSearchAdapter(
            requireContext(),
            products,
            categories
        )

        loadingLL = view.findViewById(R.id.search_ll_loading)
        loadingTv = view.findViewById(R.id.search_txt_loading)
        loadingIv = view.findViewById(R.id.search_iv_loading)
        Glide.with(requireContext()).load(R.drawable.loading)
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(loadingIv)

        searchBox = view.findViewById(R.id.search_edt_search)
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
                        searchTimer = Timer()
                        searchTimer!!.schedule(object : TimerTask() {
                            override fun run() {
                                searchTask()
                            }
                        }, searchDelay)
                    }
                }
            )
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        Firebase.database.child(ControllerProduct().table)
            .limitToFirst(pageCount)
            .get().addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                task.result?.children?.let { children ->
                    products.clear()
                    children.forEach {
                        products.add(it.getValue(Product::class.java)!!)
                    }
                }

                ControllerProductCategory().getAllAsync(
                    successListener = object : ControllerBase.SuccessListener() {
                        override fun run(dataSnapshot: DataSnapshot?) {
                            dataSnapshot?.children?.let { children ->
                                categories.clear()
                                children.forEach {
                                    categories.add(it.getValue(ProductCategory::class.java)!!)
                                }
                            }

                            (productRecycler.adapter as ProductSearchAdapter)
                                .updateList(products, categories)

                            // enable once data is loaded
                            searchBox.isEnabled = true
                        }
                    },
                    null
                )
            }
    }

    private val onScrollListener = object : NestedScrollView.OnScrollChangeListener {
        override fun onScrollChange(
            v: NestedScrollView,
            scrollX: Int,
            scrollY: Int,
            oldScrollX: Int,
            oldScrollY: Int
        ) {
            val pair = Pair(
                productRecycler.measuredHeight + productRecycler.top,
                scrollY + v.height
            )

            if (abs(pair.first - pair.second) < pageScrollDiff + loadingLL.measuredHeight) {
                if (pageScrollTimer == null) {
                    pageScrollTimer = Timer()
                    pageScrollTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            Firebase.database.child(ControllerProduct().table)
                                .orderByKey()
                                .startAt(products.last().id).limitToFirst(pageCount)
                                .get().addOnCompleteListener { task ->
                                    loadingTv.visibility = View.GONE
                                    loadingIv.visibility = View.VISIBLE

                                    if (!task.isSuccessful) return@addOnCompleteListener

                                    task.result?.children?.let { children ->
                                        val oldSize = products.size

                                        children.forEach { entry ->
                                            entry.getValue(Product::class.java)!!.let { prod ->
                                                if (!products.contains(prod)) {
                                                    products.add(prod)
                                                }
                                            }
                                        }

                                        if (oldSize != products.size)
                                            (productRecycler.adapter as ProductSearchAdapter).updateList(
                                                products, null
                                            )
                                        else {
                                            loadingTv.visibility = View.VISIBLE
                                            loadingIv.visibility = View.GONE
                                        }
                                    }
                                }
                        }
                    }, pageScrollDelay)
                }
            } else {
                pageScrollTimer?.let {
                    it.cancel()
                    pageScrollTimer = null
                }
            }
        }
    }
}
