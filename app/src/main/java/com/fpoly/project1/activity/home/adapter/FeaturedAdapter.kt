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
import com.fpoly.project1.firebase.model.Product

class FeaturedAdapter(private val context: Context, private val products: List<Product>) :
    RecyclerView.Adapter<FeaturedAdapter.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.item_recycler_featured_products, parent))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        Glide.with(context).load(product.thumbnails?.get(0)).into(holder.productThumbnail)

        holder.productName.text = product.name
        holder.productPrice.text = product.price
        holder.itemView.setOnClickListener {
            val bundleData = Bundle()
            bundleData.putString("id", product.__id)

            val intentData = Intent(context, ProductDetails::class.java)
            intentData.putExtras(bundleData)

            context.startActivity(intentData)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productThumbnail: ImageView
        var productName: TextView
        var productPrice: TextView

        init {
            productThumbnail = itemView.findViewById(R.id.item_iv_product_featured)
            productName = itemView.findViewById(R.id.item_txt_featured_name)
            productPrice = itemView.findViewById(R.id.item_txt_featured_price)
        }
    }
}