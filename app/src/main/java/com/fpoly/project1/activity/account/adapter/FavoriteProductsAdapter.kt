package com.fpoly.project1.activity.account.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot

class FavoriteProductsAdapter(
    private val context: Context,
    private val customer: Customer,
    private var products: MutableList<String>,
    private var categories: List<ProductCategory>
) : RecyclerView.Adapter<FavoriteProductsAdapter.ViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val controllerCustomer = ControllerCustomer()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.item_recycler_favorite, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ControllerProduct().getAsync(
            products[holder.absoluteAdapterPosition],
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val product = dataSnapshot?.getValue(Product::class.java)!!

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
                    holder.itemView.setOnClickListener {
                        val bundleData = Bundle()
                        bundleData.putString("id", product.id)

                        val fragment = ProductDetails()
                        fragment.arguments = bundleData
                        fragment.show(
                            (context as AppCompatActivity).supportFragmentManager,
                            "product_details"
                        )
                    }
                    holder.favoriteStatus.setOnClickListener {
                        products.removeAt(holder.absoluteAdapterPosition)

                        customer.favoriteIds?.let {
                            val newFavoriteList = it.toMutableList()
                            newFavoriteList.removeAt(holder.absoluteAdapterPosition)

                            customer.favoriteIds = newFavoriteList
                        }

                        controllerCustomer.setAsync(customer, true,
                            successListener = object : ControllerBase.SuccessListener() {
                                override fun run() {
                                    Toast.makeText(
                                        context,
                                        "Removed from favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    notifyItemRemoved(holder.absoluteAdapterPosition)
                                }
                            },
                            failureListener = object : ControllerBase.FailureListener() {
                                override fun run(error: Exception?) {
                                    Toast.makeText(
                                        context,
                                        "Unable to remove from favorites",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            })
                    }
                }
            },
            failureListener = null
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun updateList(newProductList: List<String>, newCategoryList: List<ProductCategory>?) {
        if (newCategoryList != null)
            categories = newCategoryList

        notifyItemRangeRemoved(0, products.size)
        products = newProductList.toMutableList()
        notifyItemRangeInserted(0, newProductList.size)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productThumbnail: ImageView
        var productName: TextView
        var productType: TextView
        var productPrice: TextView
        var favoriteStatus: ImageView

        init {
            productThumbnail = itemView.findViewById(R.id.item_iv_products_favorite)
            productName = itemView.findViewById(R.id.item_txt_favorite_name)
            productType = itemView.findViewById(R.id.item_txt_favorite_type)
            productPrice = itemView.findViewById(R.id.item_txt_favorite_price)
            favoriteStatus = itemView.findViewById(R.id.item_iv_favorite)
        }
    }
}
