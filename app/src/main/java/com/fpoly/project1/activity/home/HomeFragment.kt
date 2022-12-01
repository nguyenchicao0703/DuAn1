package com.fpoly.project1.activity.home

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fpoly.project1.R
import com.fpoly.project1.activity.home.adapter.FeaturedAdapter
import com.fpoly.project1.activity.home.adapter.MenuAdapter
import com.fpoly.project1.firebase.controller.ControllerCustomer
import com.fpoly.project1.firebase.controller.ControllerProduct

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val controllerCustomer = ControllerCustomer()
    private val controllerProduct = ControllerProduct()
    private lateinit var statusBarWelcomeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarWelcomeView = requireActivity().findViewById(R.id.home_txt_statusBar_title)

        val email = requireActivity().getSharedPreferences("cheetah", Context.MODE_PRIVATE)
            .getString("email", null)

        for (account in controllerCustomer.getAllSync()!!) {
            if (account.emailAddress == email) {
                statusBarWelcomeView.text =
                    "Hello, " + account.fullName!!.split("/ /g".toRegex()).toTypedArray()[-1]
            }
        }

        val menuView =
            requireActivity().findViewById<RecyclerView>(R.id.home_recyclerView_product_menu)
        menuView.adapter = MenuAdapter(requireContext(), controllerProduct.getAllSync()!!)

        // TODO implement featured products by purchase amounts for past week (or a period of time)
        val featuredView =
            requireActivity().findViewById<RecyclerView>(R.id.home_recyclerView_product_featured)
        featuredView.adapter = FeaturedAdapter(requireContext(), controllerProduct.getAllSync()!!)
    }
}