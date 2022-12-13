package com.fpoly.project1.activity.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.adapter.OrderHistoryAdapter
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerOrder
import com.fpoly.project1.firebase.enums.OrderStatus
import com.fpoly.project1.firebase.model.Order
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccountOrderHistory : BottomSheetDialogFragment() {
    private lateinit var searchBox: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: ConstraintLayout
    private lateinit var mainView: ConstraintLayout

    private val controllerOrder = ControllerOrder()
    private val orders = ArrayList<Order>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.account_order_history, container, false)
        view.alpha = 0f

        mainView = view.findViewById(R.id.orderhistory_normal_view)
        emptyView = view.findViewById(R.id.orderhistory_empty_view)

        searchBox = view.findViewById(R.id.orderhistory_edt_search)
        recyclerView = view.findViewById(R.id.orderhistory_recyclerview)

        Firebase.database.child(controllerOrder.table)
            .orderByChild("customerId").equalTo(SessionUser.sessionId)
            .get().addOnCompleteListener { task ->
                task.result?.children?.forEach { entry ->
                    entry.getValue(Order::class.java)?.let { order ->
                        orders.add(order)
                    }
                }

                if (orders.size > 0) {
                    mainView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE

                    recyclerView.adapter = OrderHistoryAdapter(requireContext(), orders)
                }

                view.animate().alpha(1f)
            }

        searchBox.doOnTextChanged { text, _, _, _ ->
            text?.let {
                (recyclerView.adapter as? OrderHistoryAdapter)?.updateList(
                    if (it.contains("status:pending")) {
                        orders.filter { order -> order.status == OrderStatus.PENDING }
                    } else if (it.contains("status:on the way")) {
                        orders.filter { order -> order.status == OrderStatus.ON_THE_WAY }
                    } else if (it.contains("status:delivered")) {
                        orders.filter { order -> order.status == OrderStatus.DELIVERED }
                    } else if (it.contains("status:cancelled")) {
                        orders.filter { order -> order.status == OrderStatus.CANCELLED }
                    } else {
                        orders
                    }
                )
            }
        }

        return view
    }
}
