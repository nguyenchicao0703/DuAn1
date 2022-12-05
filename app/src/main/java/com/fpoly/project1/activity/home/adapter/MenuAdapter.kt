package com.fpoly.project1.activity.home.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory

class MenuAdapter(private val context: Context, private val products: List<Product>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val categories = ControllerProductCategory().getAllSync()!!

    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.item_recycler_menu, parent))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        Glide.with(context).load(product.thumbnails?.get(0)).into(holder.productThumbnail)

        holder.productName.text = product.name
        holder.productPrice.text = product.price
        holder.productType.text = categories.filter { productCategory: ProductCategory ->
            productCategory.id.equals(product.categoryId)
        }[0].name
        holder.itemView.setOnClickListener {
            val bundleData = Bundle()
            bundleData.putString("id", product.id)

            val intentData = Intent(context, ProductDetails::class.java)
            intentData.putExtras(bundleData)

            context.startActivity(intentData)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productThumbnail: ImageView
        var productName: TextView
        var productType: TextView
        var productPrice: TextView

        init {
            productThumbnail = itemView.findViewById(R.id.item_iv_products_menu)
            productName = itemView.findViewById(R.id.item_txt_menu_name)
            productType = itemView.findViewById(R.id.item_txt_menu_type)
            productPrice = itemView.findViewById(R.id.item_txt_menu_price)
        }
    }
}
