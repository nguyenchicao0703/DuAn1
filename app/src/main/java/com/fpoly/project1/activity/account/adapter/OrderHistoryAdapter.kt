package com.fpoly.project1.activity.account.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.AccountOrderDetails
import com.fpoly.project1.firebase.enums.OrderStatus
import com.fpoly.project1.firebase.model.Order
import java.text.NumberFormat
import java.text.SimpleDateFormat

class OrderHistoryAdapter(
    private val context: Context,
    private var orders: List<Order>
) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemId: TextView = itemView.findViewById(R.id.item_orderhistory_txt_name)
        val itemDate: TextView = itemView.findViewById(R.id.item_orderhistory_txt_type)
        val itemCost: TextView = itemView.findViewById(R.id.item_orderhistory_txt_price)
        val itemStatus: TextView = itemView.findViewById(R.id.item_orderhistory_txt_stat)

        init {
            itemView.alpha = 0f
            itemView.translationY = 50f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.account_item_order_history, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        holder.itemId.text = "Order #${order.id}"
        holder.itemDate.text = SimpleDateFormat.getDateInstance().format(order.date!!)
        holder.itemCost.text =
            NumberFormat.getIntegerInstance().format(order.amountTotal!! + order.amountFees!!)
        holder.itemStatus.text =
            when (order.status) {
                OrderStatus.PENDING -> "Pending"
                OrderStatus.ON_THE_WAY -> "On the way"
                OrderStatus.DELIVERED -> "Delivered"
                OrderStatus.CANCELLED -> "Cancelled"
                else -> "Error"
            }

        holder.itemView.setOnClickListener {
            val fragment = AccountOrderDetails()
            fragment.arguments = bundleOf(Pair("id", order.id))
            fragment.show(
                (context as AppCompatActivity).supportFragmentManager,
                "order_details"
            )
        }

        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
    }

    fun updateList(newList: List<Order>) {
        notifyItemRangeRemoved(0, orders.size)
        orders = newList
        notifyItemRangeInserted(0, newList.size)
    }

    override fun getItemCount(): Int = orders.size
}