package com.fpoly.project1.activity.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.adapter.FavoriteProductsAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot

class AccountFavorites : BottomSheetDialogFragment() {
    private val controllerCustomer = ControllerCustomer()
    private lateinit var favoriteProductsAdapter: FavoriteProductsAdapter

    private lateinit var mainView: ConstraintLayout
    private lateinit var emptyView: ConstraintLayout

    private lateinit var searchBox: ConstraintLayout
    private lateinit var backButton: ImageView
    private lateinit var favoriteRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.account_favorite_prods, container, false)
        view.alpha = 0f

        mainView = view.findViewById(R.id.favorite_normal_view)
        emptyView = view.findViewById(R.id.favorite_empty_view)

        favoriteRecyclerView = view.findViewById(R.id.favorite_recycler_favorite)

        searchBox = view.findViewById(R.id.favorite_constraint_search)
        searchBox.visibility = View.GONE

        backButton = view.findViewById(R.id.favorite_product_iv_back)
        backButton.setOnClickListener { dismiss() }

        controllerCustomer.getAsync(
            SessionUser.sessionId,
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val customer = dataSnapshot?.getValue(Customer::class.java)!!

                    if (customer.favoriteIds == null) {
                        view.animate().alpha(1f)

                        return
                    }

                    ControllerProductCategory().getAllAsync(
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run(dataSnapshot: DataSnapshot?) {
                                val categories = ArrayList<ProductCategory>()
                                dataSnapshot?.children?.forEach {
                                    categories.add(it.getValue(ProductCategory::class.java)!!)
                                }

                                if (context == null) return

                                favoriteProductsAdapter = FavoriteProductsAdapter(
                                    requireContext(),
                                    customer,
                                    customer.favoriteIds!!,
                                    categories,
                                    view
                                )
                                favoriteRecyclerView.adapter = favoriteProductsAdapter

                                mainView.visibility = View.VISIBLE
                                emptyView.visibility = View.GONE
                                view.animate().alpha(1f)
                            }
                        },
                        failureListener
                    )
                }
            },
            failureListener
        )

        return view
    }

    private val failureListener = object : ControllerBase.FailureListener() {
        override fun run(error: Exception?) {
            Toast.makeText(
                requireContext(), "Failed to retrieve data from server", Toast
                    .LENGTH_SHORT
            ).show()

            view?.animate()?.alpha(1f)
        }
    }
}
