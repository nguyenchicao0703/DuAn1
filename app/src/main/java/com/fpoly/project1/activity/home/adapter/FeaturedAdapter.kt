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
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Product
import java.text.NumberFormat

class FeaturedAdapter(
    private val context: Context,
    private val products: List<Pair<String, Long>>
) : RecyclerView.Adapter<FeaturedAdapter.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = products.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.home_item_recycler_featured, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Firebase.database.child(ControllerProduct().table).child(products[position].first).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.getValue(Product::class.java)?.let { product ->
                        holder.productThumbnail.setImageResource(R.mipmap.iv_item_hamburger)
                        product.thumbnails?.let {
                            Glide.with(context).load(
                                it.getOrNull(0)
                                    ?: "https://cdn.discordapp.com/emojis/967451516573220914.webp"
                            ).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.productThumbnail)
                        }

                        holder.productSales.text = holder.productSales.text.toString()
                                .replace("%sales%", products[position].second.toString(), true)
                        holder.productName.text = product.name
                        holder.productPrice.text =
                            NumberFormat.getIntegerInstance().format(product.price!!)
                        holder.itemView.setOnClickListener {
                            val fragment = ProductDetails()
                            fragment.arguments = bundleOf(Pair("id", product.id))
                            fragment.show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "product_details"
                            )
                        }
                    }
                } else {
                    holder.productName.text = "Not found"
                    holder.productPrice.text = "Unknown"
                }

                holder.itemView.animate()
                    .alpha(1f)
                    .translationX(0f)
            }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productThumbnail: ImageView
        var productName: TextView
        var productPrice: TextView
        var productSales: TextView

        init {
            itemView.alpha = 0f
            itemView.translationX = 50f

            productSales = itemView.findViewById(R.id.item_txt_featured_sales)
            productThumbnail = itemView.findViewById(R.id.item_iv_product_featured)
            productName = itemView.findViewById(R.id.item_txt_featured_name)
            productPrice = itemView.findViewById(R.id.item_txt_featured_price)
        }
    }
}
