package com.fpoly.project1.activity.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.adapter.OrderHistoryAdapter
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerOrder
import com.fpoly.project1.firebase.model.Order
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot

class AccountOrderHistory : BottomSheetDialogFragment() {
    private lateinit var searchBox: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: OrderHistoryAdapter

    private val controllerOrder = ControllerOrder()
    private val orders = ArrayList<Order>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.account_order_history, container, false)

        searchBox = view.findViewById(R.id.orderhistory_edt_search)
        recyclerView = view.findViewById(R.id.orderhistory_recyclerview)

        Firebase.database.child(controllerOrder.table)
            .orderByChild("customerId").equalTo(SessionUser.sessionId)
            .get().addOnCompleteListener { task ->
                task.result?.children?.forEach { entry ->
                    entry.getValue(Order::class.java)?.let { order ->
                        if (order.customerId == SessionUser.sessionId)
                            orders.add(order)
                    }
                }

                recyclerAdapter = OrderHistoryAdapter(requireContext(), orders)
                recyclerView.adapter = recyclerAdapter
            }

        searchBox.doOnTextChanged { text, _, _, _ ->
            text?.let {
                recyclerAdapter.updateList(
                    if (it.contains("status:pending")) {
                        orders.filter { order -> order.status == 0 }
                    } else if (it.contains("status:on the way")) {
                        orders.filter { order -> order.status == 1 }
                    } else if (it.contains("status:delivered")) {
                        orders.filter { order -> order.status == 2 }
                    } else if (it.contains("status:cancelled")) {
                        orders.filter { order -> order.status == 3 }
                    } else {
                        orders
                    }
                )
            }
        }

        return view
    }
}
