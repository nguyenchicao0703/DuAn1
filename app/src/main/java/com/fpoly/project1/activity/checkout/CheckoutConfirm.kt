package com.fpoly.project1.activity.checkout

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.fpoly.project1.R
import com.fpoly.project1.firebase.SessionUser
import com.fpoly.project1.firebase.controller.ControllerBase
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerOrder
import com.fpoly.project1.firebase.model.Customer
import com.fpoly.project1.firebase.model.Order
import com.google.firebase.database.DataSnapshot
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

        controllerCustomer.getAsync(SessionUser.sessionId,
        successListener = object : ControllerBase.SuccessListener() {
            override fun run(dataSnapshot: DataSnapshot?) {
                val account = dataSnapshot?.getValue(Customer::class.java)!!

                var subTotal = 0L
                SessionUser.cart.forEach { subTotal += it.first.price!! * it.second }
                val subFees = subTotal * 0.1

                // bindings
                paymentHolder = findViewById(R.id.payment_confirmation_txt_name)
                paymentHolder.text = account.fullName
                paymentSubtotal = findViewById(R.id.payment_confirmation_txt_subtotal)
                paymentSubtotal.text = subTotal.toString()
                paymentTax = findViewById(R.id.payment_confirmation_txt_tax)
                paymentTax.text = subFees.toString()
                paymentTotal = findViewById(R.id.payment_confirmation_txt_total)
                paymentTotal.text = (subTotal + subFees).toString()

                findViewById<ImageView>(R.id.payment_confirmation_iv_back).setOnClickListener { finish() }
                findViewById<AppCompatButton>(R.id.payment_confirmation_btn_confirm).setOnClickListener {
                    val list = HashMap<String, Int>()
                    SessionUser.cart.forEach { list[it.first.id!!] = it.second }

                    controllerOrder.setAsync(
                        Order(
                            "",
                            account.id!!,
                            Date().time.toString(),
                            0,
                            list
                        ), false,
                        successListener = object : ControllerBase.SuccessListener() {
                            override fun run() {
                                Toast.makeText(this@CheckoutConfirm, "Mock payment succeeded", Toast
                                    .LENGTH_SHORT).show()
                            }
                        },
                        failureListener = object : ControllerBase.FailureListener() {
                            override fun run(error: Exception?) {
                                Toast.makeText(this@CheckoutConfirm, "Failed to place order", Toast
                                    .LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        },
        failureListener = null)
    }
}
