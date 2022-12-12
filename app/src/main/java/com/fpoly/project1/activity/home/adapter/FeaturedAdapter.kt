package com.fpoly.project1.activity.home.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.model.Product
import java.text.NumberFormat
import java.util.*

class FeaturedAdapter(private val context: Context, private val products: List<Product>) :
    RecyclerView.Adapter<FeaturedAdapter.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.item_recycler_featured_products, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        product.thumbnails?.let {
            Glide.with(context).load(
                it.getOrNull(0) ?: "https://cdn.discordapp.com/emojis/967451516573220914.webp"
            ).into(holder.productThumbnail)
        }

        holder.productName.text = product.name
        holder.productPrice.text = NumberFormat.getIntegerInstance().format(product.price)
        holder.itemView.setOnClickListener {
            val fragment = ProductDetails()
            fragment.arguments = bundleOf(Pair("id", product.id))
            fragment.show(
                (context as AppCompatActivity).supportFragmentManager, "product_details"
            )
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
