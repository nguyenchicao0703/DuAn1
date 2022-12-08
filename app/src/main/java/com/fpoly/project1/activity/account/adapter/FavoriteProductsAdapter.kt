package com.fpoly.project1.activity.account.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory

class FavoriteProductsAdapter(
    private val context: Context,
    products: List<Product>,
    customer: Customer
) : RecyclerView.Adapter<FavoriteProductsAdapter.ViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val controllerCustomer = ControllerCustomer()
    private val categories = ControllerProductCategory().getAllSync()
    private val customer: Customer
    private var productList: MutableList<Product>

    init {
        productList = products.toMutableList()
        this.customer = customer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.item_recycler_favorite, parent))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        Glide.with(context).load(product.thumbnails?.get(0)).into(holder.productThumbnail)

        holder.productName.text = product.name
        holder.productPrice.text = product.price.toString()
        holder.productType.text =
            categories!!.filter { productCategory: ProductCategory ->
                productCategory.id.equals(
                    product.categoryId
                )
            }[0].name
        holder.itemView.setOnClickListener {
            val bundleData = Bundle()
            bundleData.putString("id", product.id)

            val intentData = Intent(context, ProductDetails::class.java)
            intentData.putExtras(bundleData)

            context.startActivity(intentData)
        }
        holder.favoriteStatus.setOnClickListener {
            productList.removeAt(position)

            customer.favoriteIds?.let {
                val newFavoriteList = it.toMutableList()
                newFavoriteList.removeAt(position)

                customer.favoriteIds = newFavoriteList
            }
            if (controllerCustomer.setSync(customer, true)) { // update account
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                notifyItemRemoved(position)
            } else {
                Toast.makeText(context, "Unable to remove from favorites", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateList(newList: List<Product>) {
        notifyItemRangeRemoved(0, productList.size)
        productList = newList.toMutableList()
        notifyItemRangeInserted(0, newList.size)
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
