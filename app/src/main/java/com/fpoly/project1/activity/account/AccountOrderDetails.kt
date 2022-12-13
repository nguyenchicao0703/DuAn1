package com.fpoly.project1.activity.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.adapter.OrderDetailsAdapter
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerOrder
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.enums.OrderStatus
import com.fpoly.project1.firebase.model.Order
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import java.text.NumberFormat
import java.text.SimpleDateFormat

class AccountOrderDetails : BottomSheetDialogFragment() {
    private var mainView: View? = null

    private var order: Order? = null
    private val categories = ArrayList<ProductCategory>()

    private lateinit var backButton: ImageView
    private lateinit var orderId: TextView
    private lateinit var orderDate: TextView
    private lateinit var orderTotal: TextView
    private lateinit var orderStatus: TextView
    private lateinit var orderList: RecyclerView
    private lateinit var orderListAdapter: OrderDetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.account_order_details, container, false)
        mainView = view

        bindViews(view)

        runFirst()

        return view
    }

    private fun bindViews(view: View) {
        mainView?.alpha = 0f

        backButton = view.findViewById(R.id.order_detail_history_iv_back)
        backButton.setOnClickListener { dismiss() }

        orderId = view.findViewById(R.id.order_detail_history_txt_code)
        orderDate = view.findViewById(R.id.order_detail_history_txt_date)
        orderTotal = view.findViewById(R.id.order_detail_history_txt_total)
        orderStatus = view.findViewById(R.id.order_detail_history_txt_status)
        orderList = view.findViewById(R.id.order_detail_history_recycler)
    }

    private fun runFirst() {
        ControllerOrder().getAsync(
            requireArguments().getString("id"),
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    order = dataSnapshot?.getValue(Order::class.java)

                    runSecond()
                }
            },
            failureListener = null
        )
    }

    private fun runSecond() {
        ControllerProductCategory().getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    dataSnapshot?.children?.forEach { entry ->
                        categories.add(entry.getValue(ProductCategory::class.java)!!)
                    }

                    runThird()
                }
            },
            failureListener = null
        )
    }

    private fun runThird() {
        if (context == null) return

        order?.let {
            orderId.text = it.id
            orderDate.text = SimpleDateFormat.getDateInstance().format(it.date)
            orderTotal.text =
                NumberFormat.getIntegerInstance().format(
                    order!!.amountTotal!! + order!!.amountFees!!
                )
            orderStatus.text = when (it.status) {
                OrderStatus.PENDING -> "Pending"
                OrderStatus.ON_THE_WAY -> "On the way"
                OrderStatus.DELIVERED -> "Delivered"
                OrderStatus.CANCELLED -> "Cancelled"
                else -> "Unknown"
            }
            orderListAdapter = OrderDetailsAdapter(
                requireContext(),
                order!!.list!!,
                categories
            )
            orderList.adapter = orderListAdapter
        }

        mainView?.animate()?.alpha(1f)
    }
}