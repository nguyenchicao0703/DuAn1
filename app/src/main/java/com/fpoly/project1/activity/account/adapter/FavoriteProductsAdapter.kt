package com.fpoly.project1.activity.account.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot
import java.text.NumberFormat

class FavoriteProductsAdapter(
    private val context: Context,
    private val customer: Customer,
    private var products: List<String>,
    private var categories: List<ProductCategory>,
    private val view: View
) : RecyclerView.Adapter<FavoriteProductsAdapter.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val mainView = view.findViewById<ConstraintLayout>(R.id.favorite_normal_view)
    private val emptyView = view.findViewById<ConstraintLayout>(R.id.favorite_empty_view)
    private val controllerCustomer = ControllerCustomer()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.account_item_recycler_favorite, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println(products)

        // get the related product at position
        ControllerProduct().getAsync(
            products[holder.absoluteAdapterPosition],
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    // product object
                    dataSnapshot?.getValue(Product::class.java)?.let { product ->
                        // load thumbnail
                        product.thumbnails?.let {
                            Glide.with(context).load(
                                it.getOrNull(0)
                                    ?: "https://cdn.discordapp.com/emojis/967451516573220914.webp"
                            ).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.productThumbnail)
                        }

                        // name
                        holder.productName.text = product.name

                        // price
                        holder.productPrice.text =
                            NumberFormat.getIntegerInstance().format(product.price!!)

                        // product category
                        holder.productType.text =
                            categories.filter { productCategory: ProductCategory ->
                                productCategory.id.equals(
                                    product.categoryId
                                )
                            }.getOrNull(0)?.name ?: "Unknown"

                        // show product details fragment
                        holder.itemView.setOnClickListener {
                            val fragment = ProductDetails()
                            fragment.arguments = bundleOf(Pair("id", product.id))
                            fragment.show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "product_details"
                            )
                        }

                        // favorite status handling
                        holder.favoriteStatus.setOnClickListener {
                            val mutableProducts = products.toMutableList()
                            mutableProducts.removeAt(holder.absoluteAdapterPosition)
                            products = mutableProducts

                            // if the user has a favorite list, clone it, remove the
                            // corresponding entry then override
                            customer.favoriteIds?.let {
                                val newFavoriteList = it.toMutableList()
                                newFavoriteList.removeAt(holder.absoluteAdapterPosition)

                                customer.favoriteIds = newFavoriteList
                            }

                            // update the user object in Firebase
                            controllerCustomer.setAsync(customer, true,
                                successListener = object : ControllerBase.SuccessListener() {
                                    override fun run() {
                                        Toast.makeText(
                                            context,
                                            "Removed from favorites",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // notify removed at index
                                        notifyItemRemoved(holder.absoluteAdapterPosition)

                                        // change view
                                        if (customer.favoriteIds!!.isEmpty()) {
                                            mainView.visibility = View.GONE
                                            emptyView.visibility = View.VISIBLE
                                        }
                                    }
                                },
                                failureListener = object : ControllerBase.FailureListener() {
                                    override fun run(error: Exception?) {
                                        Toast.makeText(
                                            context,
                                            "Unable to remove from favorites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }

                        holder.itemView.animate()
                            .alpha(1f)
                            .translationY(0f)
                    }
                }
            },
            failureListener = null
        )
    }

    override fun getItemCount(): Int = products.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productThumbnail: ImageView
        var productName: TextView
        var productType: TextView
        var productPrice: TextView
        var favoriteStatus: ImageView

        init {
            itemView.alpha = 0f
            itemView.translationY = 50f

            productThumbnail = itemView.findViewById(R.id.item_iv_products_favorite)
            productName = itemView.findViewById(R.id.item_txt_favorite_name)
            productType = itemView.findViewById(R.id.item_txt_favorite_type)
            productPrice = itemView.findViewById(R.id.item_txt_favorite_price)
            favoriteStatus = itemView.findViewById(R.id.item_iv_favorite)
        }
    }
}
