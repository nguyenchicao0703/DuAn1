package com.fpoly.project1.activity.publish.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.product.ProductDetails
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import java.text.NumberFormat
import java.util.*

class PublishOverviewAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private var categories: ArrayList<ProductCategory>
) : RecyclerView.Adapter<PublishOverviewAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.item_recycler_menu, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[holder.absoluteAdapterPosition]

        product.thumbnails?.let {
            Glide.with(context).load(
                it.getOrNull(0) ?: "https://cdn.discordapp.com/emojis/967451516573220914.webp"
            ).into(holder.productThumbnail)
        }

        holder.productName.text = product.name
        holder.productPrice.text = NumberFormat.getIntegerInstance().format(product.price)
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
                (context as AppCompatActivity).supportFragmentManager, "product_details"
            )
        }
        holder.itemView.setOnLongClickListener {
            val dialog = Dialog(context)

            dialog.setContentView(R.layout.sell_dialog_remove)
            dialog.findViewById<TextView>(R.id.dialog_remove_sell_txt_name).text = product.name
            dialog.findViewById<TextView>(R.id.dialog_remove_sell_txt_price).text =
                NumberFormat.getInstance().format(product.price)
            dialog.findViewById<Button>(R.id.dialog_remove_sell_btn_cancle).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.dialog_remove_sell_btn_sure).setOnClickListener {
                ControllerProduct().removeAsync(product.id,
                    successListener = object : ControllerBase.SuccessListener() {
                        override fun run() {
                            Toast.makeText(context, "Successfully removed item", Toast.LENGTH_SHORT)
                                .show()

                            list.removeAt(holder.absoluteAdapterPosition)
                            notifyItemRemoved(holder.absoluteAdapterPosition)

                            dialog.dismiss()
                        }
                    },
                    failureListener = object : ControllerBase.FailureListener() {
                        override fun run(error: Exception?) {
                            Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            }
            dialog.show()

            true
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(
        newProductList: ArrayList<Product>,
        newCategoryList: ArrayList<ProductCategory>?
    ) {
        println("Trying to update recycler")
        if (newCategoryList != null)
            this.categories = newCategoryList

        this.list = newProductList
        notifyDataSetChanged()
    }

    fun addItem(product: Product) {
        this.list.add(product)

        notifyItemInserted(list.size - 1)
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

