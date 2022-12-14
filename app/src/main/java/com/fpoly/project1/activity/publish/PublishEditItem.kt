package com.fpoly.project1.activity.publish

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fpoly.project1.R
import com.fpoly.project1.activity.enums.RequestCode
import com.fpoly.project1.activity.publish.adapter.PublishOverviewAdapter
import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerProduct
import com.fpoly.project1.firebase.controller.ControllerProductCategory
import com.fpoly.project1.firebase.model.Product
import com.fpoly.project1.firebase.model.ProductCategory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import java.time.Instant

class PublishEditItem : BottomSheetDialogFragment() {
    private lateinit var backButton: ImageView

    private lateinit var itemThumbnail: ImageView
    private lateinit var itemName: EditText
    private lateinit var itemPrice: EditText
    private lateinit var itemCategory: Spinner
    private lateinit var itemDescription: EditText
    private lateinit var itemPublish: AppCompatButton

    private val categories = ArrayList<ProductCategory>()
    private val controllerProduct = ControllerProduct()
    private val controllerProductCategory = ControllerProductCategory()

    private val thumbnails = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.publish_edit_item, container, false)

        ControllerProduct().getAsync(requireArguments().getString("id"),
            successListener = object : ControllerBase.SuccessListener() {
                override fun run(dataSnapshot: DataSnapshot?) {
                    val product = dataSnapshot?.getValue(Product::class.java)

                    if (product == null) {
                        Toast.makeText(
                            requireContext(), "Failed to retrieve data from server",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    itemName.setText(product.name)
                    itemDescription.setText(product.description)
                    itemPrice.setText(product.price.toString())
                    product.thumbnails?.let {
                        thumbnails.clear()
                        thumbnails.add(it[0])
                        Glide.with(requireContext()).load(it[0])
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(itemThumbnail)
                    }

                    itemThumbnail.setOnClickListener {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            RequestCode.PROFILE_IMAGE_UPLOAD
                        )
                    }

                    controllerProductCategory.getAllAsync(
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run(dataSnapshot: DataSnapshot?) {
                                dataSnapshot?.children?.forEach { entry ->
                                    categories.add(entry.getValue(ProductCategory::class.java)!!)
                                }

                                if (context != null) {
                                    itemCategory.adapter =
                                        ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_list_item_1,
                                            categories.map { cat -> cat.name }
                                        )
                                    itemCategory.setSelection(categories.indexOfFirst { cat ->
                                        cat.id.equals(product.categoryId)
                                    })
                                }
                            }
                        },
                        failureListener = null
                    )

                    itemPublish.setOnClickListener {
                        var hasErrors = false
                        if (itemName.text.isEmpty()) {
                            itemName.error = "This field is required"
                            hasErrors = true
                        }
                        if (itemPrice.text.isEmpty()) {
                            itemPrice.error = "This field is required"
                            hasErrors = true
                        }
                        if (itemDescription.text.isEmpty()) {
                            itemDescription.error = "This field is required"
                            hasErrors = true
                        }
                        try {
                            itemPrice.text.toString().toLong()
                        } catch (e: NumberFormatException) {
                            itemPrice.error = "Not a valid number"
                            hasErrors = true
                        }
                        if (!hasErrors) {
                            val updatedProduct = Product(
                                id = product.id,
                                sellerId = product.sellerId,
                                categoryId = categories[itemCategory.selectedItemPosition].id,
                                price = itemPrice.text.toString().toLong(),
                                name = itemName.text.toString(),
                                description = itemDescription.text.toString(),
                                thumbnails = thumbnails
                            )
                            controllerProduct.setAsync(
                                value = updatedProduct,
                                update = true,
                                successListener = object : ControllerBase.SuccessListener() {
                                    override fun run(unused: Any?) {
                                        Toast.makeText(
                                            requireContext(), "Successfully updated item", Toast
                                                .LENGTH_SHORT
                                        ).show()

                                        dismiss()
                                        requireActivity().findViewById<RecyclerView>(R.id.sell_recyclerView)
                                            ?.let {
                                                (it.adapter as PublishOverviewAdapter).updateItem(
                                                    updatedProduct
                                                )
                                            }
                                    }
                                },
                                failureListener = object : ControllerBase.FailureListener() {
                                    override fun run(error: Exception?) {
                                        Toast.makeText(
                                            requireContext(), "Failed to update item", Toast
                                                .LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }
            },
            failureListener = object : ControllerBase.FailureListener() {
                override fun run(error: Exception?) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to retrieve data from server",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

        view.let {
            backButton = it.findViewById(R.id.publish_iv_back)
            backButton.setOnClickListener {
                if (parentFragmentManager.backStackEntryCount > 0)
                    parentFragmentManager.popBackStack()
            }

            itemThumbnail = it.findViewById(R.id.publish_iv_avt)
            itemName = it.findViewById(R.id.publish_txt_name)
            itemPrice = it.findViewById(R.id.publish_txt_price)
            itemCategory = it.findViewById(R.id.publish_txt_category)
            itemDescription = it.findViewById(R.id.publish_txt_description)
            itemPublish = it.findViewById(R.id.publish_btn_publish)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != RequestCode.PROFILE_IMAGE_UPLOAD || resultCode != Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "User cancelled action", Toast.LENGTH_SHORT).show()
            return
        }

        data?.data?.let { requireContext().contentResolver.openInputStream(it) }?.let {
            val childRef = "/product_thumbnails/${Instant.now().toEpochMilli()}.jpg"
            Firebase.storage.child(childRef).let { storage ->
                storage.putStream(it)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            storage.downloadUrl.addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    this.thumbnails.clear()
                                    this.thumbnails.add(task2.result.toString())

                                    Glide.with(requireContext()).load(task2.result)
                                        .into(itemThumbnail)
                                }
                            }
                        }
                    }
            }
        }
    }
}