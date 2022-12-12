package com.fpoly.project1.activity.account.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.enums.OrderStatus
import com.fpoly.project1.firebase.model.Order
import com.fpoly.project1.firebase.model.Product
import com.google.firebase.database.DataSnapshot
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryAdapter(
    context: Context,
    private var orders: List<Order>
) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemId: TextView = itemView.findViewById(R.id.item_orderhistory_txt_name)
        val itemDate: TextView = itemView.findViewById(R.id.item_orderhistory_txt_type)
        val itemCost: TextView = itemView.findViewById(R.id.item_orderhistory_txt_price)
        val itemStatus: TextView = itemView.findViewById(R.id.item_orderhistory_txt_stat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.account_item_order_history, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        var priceTotal = 0L

        ControllerProduct().getAllAsync(
            object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    dataSnapshot?.children?.forEach { entry ->
                        entry.getValue(Product::class.java)?.let { product ->
                            if (order.list!!.contains(product.id))
                                priceTotal += product.price!! * order.list[product.id]!!
                        }
                    }

                    holder.itemId.text = "Order #${order.id}"
                    holder.itemDate.text = SimpleDateFormat.getDateInstance().format(order.date!!)
                    holder.itemCost.text = NumberFormat.getIntegerInstance().format(priceTotal)
                    holder.itemStatus.text =
                        when (order.status) {
                            OrderStatus.PENDING -> "Pending"
                            OrderStatus.ON_THE_WAY -> "On the way"
                            OrderStatus.DELIVERED -> "Delivered"
                            OrderStatus.CANCELLED -> "Cancelled"
                            else -> "Error"
                        }
                }
            },
            failureListener = null
        )
    }

    fun updateList(newList: List<Order>) {
        notifyItemRangeRemoved(0, orders.size)
        orders = newList
        notifyItemRangeInserted(0, newList.size)
    }

    override fun getItemCount(): Int = orders.size
}