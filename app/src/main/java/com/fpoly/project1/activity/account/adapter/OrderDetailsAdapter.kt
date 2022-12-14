package com.fpoly.project1.activity.account.adapter

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
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot
import java.text.NumberFormat

class OrderDetailsAdapter(
    private val context: Context,
    private var list: Map<String, Int>,
    private var categories: List<ProductCategory>
) : RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>() {

    private val controllerProduct = ControllerProduct()
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.account_order_item_recycler_details, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 50f

        val pair = list.toList().asReversed()[position]

        controllerProduct.getAsync(pair.first,
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val product = dataSnapshot?.getValue(Product::class.java)

                    if (product == null) {
                        holder.productName.text = "Product not found"
                        holder.productPrice.text = "Unknown"
                        holder.productType.text = "Unknown"
                        holder.productCount.text = "x0"
                    } else {
                        product.thumbnails?.let {
                            Glide.with(context).load(
                                it.getOrNull(0)
                                    ?: "https://cdn.discordapp.com/emojis/967451516573220914.webp"
                            ).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.productThumbnail)
                        }

                        holder.productName.text = product.name
                        holder.productCount.text = "x${pair.second}"
                        holder.productPrice.text =
                            NumberFormat.getIntegerInstance().format(product.price!!)
                        holder.productType.text =
                            categories.filter { productCategory: ProductCategory ->
                                productCategory.id.equals(
                                    product.categoryId
                                )
                            }.getOrNull(0)?.name ?: "Unknown"
                        holder.itemView.setOnClickListener {
                            val fragment = ProductDetails()
                            fragment.arguments = bundleOf(Pair("id", product.id))
                            fragment.show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "product_details"
                            )
                        }
                    }

                    holder.itemView.animate()
                        .alpha(1f)
                        .translationY(0F)
                }
            },
            failureListener = object : ControllerBase.FailureListener() {
                override fun run(error: Exception?) {
                    holder.productName.text = error!!.message
                    holder.itemView.animate()
                        .alpha(1f)
                        .translationY(0F)
                }
            })
    }

    override fun getItemCount(): Int = list.size

    fun updateList(
        newProductList: Map<String, Int>, newCategoryList:
        List<ProductCategory>?
    ) {
        if (newCategoryList != null && newCategoryList.isNotEmpty())
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

        init {
            productThumbnail = itemView.findViewById(R.id.item_iv_products_order)
            productName = itemView.findViewById(R.id.item_txt_order_name)
            productType = itemView.findViewById(R.id.item_txt_order_type)
            productPrice = itemView.findViewById(R.id.item_txt_order_price)
            productCount = itemView.findViewById(R.id.item_txt_order_count)
        }
    }
}
