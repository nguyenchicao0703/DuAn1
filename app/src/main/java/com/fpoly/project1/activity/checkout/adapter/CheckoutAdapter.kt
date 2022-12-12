package com.fpoly.project1.activity.checkout.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.checkout.CheckoutOverview
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import java.text.NumberFormat

class CheckoutAdapter(
    private val context: Context,
    private val fragment: CheckoutOverview,
    private var list: List<Pair<Product, Int>>,
    private var categories: List<ProductCategory>
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.item_recycler_cart, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position].first

        product.thumbnails?.let {
            Glide.with(context).load(
                it.getOrNull(0) ?: "https://cdn.discordapp.com/emojis/967451516573220914.webp"
            ).into(holder.productThumbnail)
        }

        holder.productName.text = product.name
        holder.productPrice.text = product.price.toString().format("%,d")
        holder.productType.text =
            categories.filter { productCategory: ProductCategory ->
                productCategory.id.equals(
                    product.categoryId
                )
            }.getOrNull(0)?.name ?: "Unknown"
        holder.productCount.text =
            list[holder.absoluteAdapterPosition].second.toString()
        holder.itemView.setOnClickListener {
            val fragment = ProductDetails()

            fragment.arguments = bundleOf(Pair("id", product.id))
            fragment.show(
                (context as AppCompatActivity).supportFragmentManager, "product_details"
            )
        }

        holder.productCountAdd.setOnClickListener {
            SessionUser.cart.find { item ->
                item.first.id == list[position].first.id
            }?.let { entry ->
                val index = SessionUser.cart.indexOf(entry)

                SessionUser.cart[index] = Pair(entry.first, entry.second + 1)

                notifyItemChanged(index)
                fragment.renderTotalCost()
            }
        }

        holder.productCountRemove.setOnClickListener {
            SessionUser.cart.find { item ->
                item.first.id == list[position].first.id
            }?.let { entry ->
                val index = SessionUser.cart.indexOf(entry)

                SessionUser.cart[index] =
                    Pair(entry.first, if (entry.second - 1 < 0) 1 else entry.second - 1)
                SessionUser.cart.remove(entry)

                notifyItemChanged(index)
                fragment.renderTotalCost()
            }
        }

        holder.itemView.setOnLongClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.checkout_dialog_remove)
            dialog.findViewById<TextView>(R.id.dialog_remove_cart_txt_name).text = product.name
            dialog.findViewById<TextView>(R.id.dialog_remove_cart_txt_amout).text =
                list[holder.absoluteAdapterPosition].second.toString()
            dialog.findViewById<TextView>(R.id.dialog_remove_cart_txt_price).text =
                NumberFormat.getIntegerInstance()
                    .format(product.price!! * list[holder.absoluteAdapterPosition].second)
            dialog.findViewById<Button>(R.id.dialog_remove_cart_btn_cancle).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.dialog_remove_cart_btn_sure).setOnClickListener {
                SessionUser.cart.find { item ->
                    item.first.id == list[position].first.id
                }?.let { entry ->
                    val index = SessionUser.cart.indexOf(entry)

                    SessionUser.cart.removeAt(index)

                    notifyItemRemoved(index)
                    fragment.renderTotalCost()

                    dialog.dismiss()
                    Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()

            true
        }
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productThumbnail: ImageView
        var productName: TextView
        var productType: TextView
        var productPrice: TextView
        var productCount: TextView
        var productCountRemove: Button
        var productCountAdd: Button

        init {
            productThumbnail = itemView.findViewById(R.id.item_iv_products_cart)
            productName = itemView.findViewById(R.id.item_txt_cart_name)
            productType = itemView.findViewById(R.id.item_txt_cart_type)
            productPrice = itemView.findViewById(R.id.item_txt_cart_price)
            productCount = itemView.findViewById(R.id.item_txt_cart_amount)

            productCountRemove = itemView.findViewById(R.id.item_button_cart_minimize)
            productCountAdd = itemView.findViewById(R.id.item_button_cart_add)
        }
    }
}
