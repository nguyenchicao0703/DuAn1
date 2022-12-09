package com.fpoly.project1.activity.checkout.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.checkout.CheckoutOverview
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot

class CheckoutAdapter(
    private val context: Context,
    private val fragment: CheckoutOverview,
    private var list: List<Pair<Product, Int>>,
    private var categories: List<ProductCategory>
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private val layoutInflater = LayoutInflater.from(context)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.item_recycler_cart, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position].first

        Glide.with(context).load(product.thumbnails?.get(0))
            .into(holder.productThumbnail)

        holder.productName.text = product.name
        holder.productPrice.text = product.price.toString()
        holder.productType.text =
            categories.filter { productCategory: ProductCategory ->
                productCategory.id.equals(
                    product.categoryId
                )
            }[0].name
        holder.productCount.text =
            list[holder.absoluteAdapterPosition].second.toString()
        holder.itemView.setOnClickListener {
            val bundleData = Bundle()
            bundleData.putString("id", product.id)

            val fragment = ProductDetails()
            fragment.arguments = bundleData
            fragment.show(
                (context as AppCompatActivity).supportFragmentManager, "product_details"
            )
        }

        holder.productCountAdd.setOnClickListener {
            SessionUser.cart.find { item ->
                item.first.id == list[position].first.id
            }?.let { entry ->
                val index = SessionUser.cart.indexOf(entry)

                SessionUser.cart.add(index, Pair(entry.first, entry.second + 1))
                SessionUser.cart.remove(entry)

                notifyItemChanged(index)
                fragment.renderTotalCost()
            }
        }
        holder.productCountRemove.setOnClickListener {
            SessionUser.cart.find { item ->
                item.first.id == list[position].first.id
            }?.let { entry ->
                val index = SessionUser.cart.indexOf(entry)

                SessionUser.cart.add(index, Pair(
                    entry.first,
                    if (entry.second - 1 < 0) 1 else entry.second - 1
                ))
                SessionUser.cart.remove(entry)

                notifyItemChanged(index)
                fragment.renderTotalCost()
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(
        newProductList: List<Pair<Product, Int>>, newCategoryList:
        List<ProductCategory>?
    ) {
        println("Trying to update recycler")
        if (newCategoryList != null)
            this.categories = newCategoryList

        notifyItemRangeRemoved(0, list.size)
        this.list = newProductList
        notifyItemRangeInserted(0, newProductList.size)
    }

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
