package com.fpoly.project1.activity.product

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fpoly.project1.R
import com.fpoly.project1.activity.chat.ChatView
import com.fpoly.project1.activity.product.adapter.ProductProfileAdapter
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct
import de.hdodenhof.circleimageview.CircleImageView

class ProductProfileView : AppCompatActivity() {
	private lateinit var productAdapter: ProductProfileAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.profile_overview_other)

		val user = ControllerCustomer().getSync(intent.extras?.getString("id", null))
		if (user == null) {
			Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show()
			return
		}

		findViewById<ImageView>(R.id.other_users_iv_back).setOnClickListener { finish() }
		findViewById<CircleImageView>(R.id.other_users_iv_avt).let {
			Glide.with(this).load(user.avatarUrl).into(it)
		}
		findViewById<TextView>(R.id.other_users_txt_user_name).text = user.fullName
		findViewById<TextView>(R.id.other_users_txt_user_email).visibility = View.GONE
		findViewById<RecyclerView>(R.id.other_users_recyclerView_product).let {
			productAdapter = ProductProfileAdapter(this,
				ControllerProduct().getAllSync()!!.filter { prod -> prod.sellerId == user.__id }
			)

			it.adapter = productAdapter
		}
		findViewById<AppCompatButton>(R.id.other_users_btn_chat).setOnClickListener {
			val bundleData = Bundle()
			bundleData.putString("id", user.__id)

			val intentData = Intent(this, ChatView::class.java)
			intentData.putExtras(bundleData)

			startActivity(intentData)
		}
	}
}