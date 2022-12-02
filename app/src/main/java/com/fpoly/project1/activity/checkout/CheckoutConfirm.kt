package com.fpoly.project1.activity.checkout

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.fpoly.project1.R
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerOrder
import com.fpoly.project1.firebase.model.Order
import java.util.*

class CheckoutConfirm : AppCompatActivity() {
	private lateinit var paymentHolder: TextView
	private lateinit var paymentSubtotal: TextView
	private lateinit var paymentTax: TextView
	private lateinit var paymentTotal: TextView
	private val controllerCustomer = ControllerCustomer()
	private val controllerOrder = ControllerOrder()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.checkout_confirm)

		val account = controllerCustomer.getSync(SessionUser.sessionId)!!

		val cartTotal = 0
		SessionUser.cart.forEach { item -> cartTotal.plus(item.second) }
		val cartTax = cartTotal * 0.05

		// bindings
		paymentHolder.let {
			paymentHolder = findViewById(R.id.payment_confirmation_txt_name)
			it.text = account.fullName
		}
		paymentSubtotal.let {
			paymentSubtotal = findViewById(R.id.payment_confirmation_txt_subtotal)
			it.text = cartTotal.toString()
		}
		paymentTax.let {
			paymentTax = findViewById(R.id.payment_confirmation_txt_tax)
			it.text = cartTax.toString()
		}
		paymentTotal.let {
			paymentTotal = findViewById(R.id.payment_confirmation_txt_total)
			it.text = (cartTotal + cartTax).toString()
		}

		findViewById<ImageView>(R.id.payment_confirmation_iv_back).setOnClickListener { finish() }
		findViewById<AppCompatButton>(R.id.payment_confirmation_btn_confirm).setOnClickListener {
			// just a mock payment, actually does nothing
			if (controllerOrder.setSync(
					Order(
						"",
						account.__id!!,
						Date().time.toString(),
						0,
						SessionUser.cart.toMap()
					), false
				)
			) {
				Toast.makeText(this, "Payment succeed", Toast.LENGTH_SHORT).show()
			} else {
				Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
			}
		}
	}
}
