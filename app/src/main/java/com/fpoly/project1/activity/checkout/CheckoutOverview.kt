package com.fpoly.project1.activity.checkout

import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.checkout.adapter.CheckoutAdapter
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot

class CheckoutOverview : Fragment(R.layout.checkout_cart) {
    override fun onResume() {
        super.onResume()

        requireActivity().findViewById<ImageView>(R.id.cart_iv_back).setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0)
                parentFragmentManager.popBackStack()
        }
        requireActivity().findViewById<Button>(R.id.cart_btn_order).setOnClickListener {
            startActivity(Intent(requireContext(), CheckoutConfirm::class.java))
        }

        requireActivity().findViewById<RecyclerView>(R.id.cart_recyclerView_product)
            .let { view ->
                ControllerProductCategory().getAllAsync(
                    successListener = object :
                        ControllerBase.SuccessListener() {
                        override fun run(dataSnapshot: DataSnapshot?) {
                            val categories = ArrayList<ProductCategory>()
                            dataSnapshot?.children?.forEach { entry ->
                                categories.add(entry.getValue(ProductCategory::class.java)!!)
                            }

                            view.adapter = CheckoutAdapter(
                                requireContext(),
                                this@CheckoutOverview,
                                SessionUser.cart, categories
                            )

                            renderTotalCost()
                        }
                    },
                    failureListener = null
                )
            }
    }

    fun renderTotalCost() {
        var subTotal = 0L
        SessionUser.cart.forEach { subTotal += it.first.price!! * it.second }
        val subFees = subTotal * 0.1

        requireActivity().findViewById<TextView>(R.id.cart_txt_price_subtotal).text =
            subTotal.toString()
        requireActivity().findViewById<TextView>(R.id.cart_txt_price_other_fees)
            .text = subFees.toString()
        requireActivity().findViewById<TextView>(R.id.cart_txt_price_total)
            .text = (subTotal + subFees).toString()
    }
}
