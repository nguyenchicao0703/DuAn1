package com.fpoly.project1.activity.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import java.text.NumberFormat

class MenuAdapter(
    private val context: Context, private val products: List<Product>, private val
    categories: List<ProductCategory>
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.home_item_recycler_menu, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        product.thumbnails?.let {
            Glide.with(context).load(
                it.getOrNull(0) ?: "https://cdn.discordapp.com/emojis/967451516573220914.webp"
            ).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.productThumbnail)
        }

        holder.productName.text = product.name
        holder.productPrice.text = NumberFormat.getIntegerInstance().format(product.price)
        holder.productType.text = categories.filter { productCategory: ProductCategory ->
            productCategory.id.equals(product.categoryId)
        }.getOrNull(0)?.name ?: "Unknown"
        holder.itemView.setOnClickListener {
            val fragment = ProductDetails()
            fragment.arguments = bundleOf(Pair("id", product.id))
            fragment.show(
                (context as AppCompatActivity).supportFragmentManager, "product_details"
            )
        }

        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productThumbnail: ImageView
        var productName: TextView
        var productType: TextView
        var productPrice: TextView

        init {
            itemView.alpha = 0f
            itemView.translationY = 50f

            productThumbnail = itemView.findViewById(R.id.item_iv_products_menu)
            productName = itemView.findViewById(R.id.item_txt_menu_name)
            productType = itemView.findViewById(R.id.item_txt_menu_type)
            productPrice = itemView.findViewById(R.id.item_txt_menu_price)
        }
    }
}
