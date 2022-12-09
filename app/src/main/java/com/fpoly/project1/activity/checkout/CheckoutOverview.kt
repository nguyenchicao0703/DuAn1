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
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot

class CheckoutOverview : Fragment(R.layout.checkout_cart) {
    private lateinit var backButton: ImageView
    private lateinit var checkoutConfirmButton: Button
    private lateinit var checkoutRecycler: RecyclerView
    private lateinit var checkoutSubTotal: TextView
    private lateinit var checkoutSubFees: TextView
    private lateinit var checkoutTotal: TextView

    override fun onResume() {
        super.onResume()

        backButton = requireActivity().findViewById(R.id.cart_iv_back)
        backButton.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0)
                parentFragmentManager.popBackStack()
        }

        checkoutSubTotal = requireActivity().findViewById(R.id.cart_txt_price_subtotal)
        checkoutSubFees = requireActivity().findViewById(R.id.cart_txt_price_other_fees)
        checkoutTotal =  requireActivity().findViewById(R.id.cart_txt_price_total)

        checkoutConfirmButton = requireActivity().findViewById(R.id.cart_btn_order)
        checkoutConfirmButton.setOnClickListener {
            startActivity(Intent(requireContext(), CheckoutConfirm::class.java))
        }

        checkoutRecycler = requireActivity().findViewById(R.id.cart_recyclerView_product)
        checkoutRecycler.let { view ->
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

        checkoutSubTotal.text = subTotal.toString()
        checkoutSubFees.text = subFees.toString()
        checkoutTotal.text = (subTotal + subFees).toString()
    }
}
