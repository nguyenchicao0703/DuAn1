package com.fpoly.project1.activity.publish.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat.animate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fpoly.project1.R
import com.fpoly.project1.activity.enums.RequestCode
import com.fpoly.project1.activity.publish.PublishEditItem
import com.fpoly.project1.activity.publish.PublishOverview
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerOrder
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.model.Order
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.firebase.database.DataSnapshot
import java.text.NumberFormat

class PublishOverviewAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private var categories: ArrayList<ProductCategory>,
    private val view: View?
) : RecyclerView.Adapter<PublishOverviewAdapter.ViewHolder>() {

    private var selectedId = -1
    private val layoutInflater = LayoutInflater.from(context)
    private val mainView = view?.findViewById<ConstraintLayout>(R.id.sell_normal_view)
    private val emptyView = view?.findViewById<ConstraintLayout>(R.id.sell_empty_view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(layoutInflater.inflate(R.layout.publish_item_recycler, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[holder.absoluteAdapterPosition]

        product.thumbnails?.let {
            // normal
            Glide.with(context).load(
                it.getOrNull(0) ?: "https://cdn.discordapp.com/emojis/967451516573220914.webp"
            ).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.productThumbnail)
        }

        holder.productId.text = product.id
        holder.productSales.text = product.sales?.toString() ?: "None"
        holder.productName.text = product.name
        holder.productPrice.text = NumberFormat.getIntegerInstance().format(product.price)
        holder.productType.text =
            categories.filter { productCategory: ProductCategory ->
                productCategory.id.equals(product.categoryId)
            }.getOrNull(0)?.name ?: "Unknown"
        ControllerOrder().getAllAsync(
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    var sales = 0

                    dataSnapshot?.children?.forEach {
                        val entry = it.getValue(Order::class.java)
                        if (entry?.list?.containsKey(product.id!!) == true)
                            sales += entry.list[product.id!!]!!
                    }

                    holder.productSales.text = sales.toString()
                }
            },
            failureListener = null
        )
        holder.editView.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = -50f
        }
        holder.btnEdit.setOnClickListener {
            val fragment = PublishEditItem()
            fragment.arguments = bundleOf(Pair("id", product.id))
            fragment.show((context as FragmentActivity).supportFragmentManager, "publish_edit")
        }
        holder.btnDelete.setOnClickListener {
            Dialog(context).apply {
                this.setContentView(R.layout.publish_dialog_remove)
                this.window?.setBackgroundDrawable(
                    ColorDrawable(
                        context.resources.getColor(
                            android.R.color.transparent,
                            context.theme
                        )
                    )
                )
                this.findViewById<TextView>(R.id.dialog_remove_sell_txt_name).text = product.name
                this.findViewById<TextView>(R.id.dialog_remove_sell_txt_price).text =
                    NumberFormat.getInstance().format(product.price)
                this.findViewById<Button>(R.id.dialog_remove_sell_btn_cancle).setOnClickListener {
                    this.dismiss()
                }
                this.findViewById<Button>(R.id.dialog_remove_sell_btn_sure).setOnClickListener {
                    ControllerProduct().removeAsync(product.id,
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run() {
                                Toast.makeText(
                                    context,
                                    "Successfully removed item",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                                list.removeAt(holder.absoluteAdapterPosition)
                                notifyItemRemoved(holder.absoluteAdapterPosition)

                                if (list.size == 0) {
                                    mainView?.visibility = View.GONE
                                    emptyView?.visibility = View.VISIBLE
                                }

                                this@apply.dismiss()
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
            }.show()
        }
    }

    override fun getItemCount(): Int = list.size
    override fun getItemId(position: Int): Long = position.toLong()

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

    fun updateItem(product: Product) {
        val index = this.list.indexOfFirst { prod -> prod.id!! == product.id!! }

        if (index != -1) {
            this.list[index] = product
            notifyItemChanged(index)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productThumbnail: ImageView = itemView.findViewById(R.id.item_iv_products_menu)
        val productName: TextView = itemView.findViewById(R.id.item_txt_menu_name)
        val productType: TextView = itemView.findViewById(R.id.item_txt_menu_type)
        val productPrice: TextView = itemView.findViewById(R.id.item_txt_menu_price)
        val productId: TextView = itemView.findViewById(R.id.item_txt_id)
        val productSales: TextView = itemView.findViewById(R.id.item_txt_sales)

        val mainView: CardView = itemView.findViewById(R.id.item_main_view)
        val editView: LinearLayout = itemView.findViewById(R.id.item_edit_view)

        val btnEdit: Button = itemView.findViewById(R.id.item_btn_edit)
        val btnDelete: Button = itemView.findViewById(R.id.item_btn_delete)

        init {
            mainView.setOnClickListener {
                editView.apply {
                    if (visibility == View.GONE) {
                        visibility = View.VISIBLE
                        animate().alpha(1f).translationY(0f).setListener(null)
                    } else {
                        animate().alpha(0f).translationY(-50f)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    visibility = View.GONE
                                }
                            })
                    }
                }
            }
        }
    }
}

