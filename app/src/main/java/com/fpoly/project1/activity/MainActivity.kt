package com.fpoly.project1.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.AccountPanel
import com.fpoly.project1.activity.chat.ChatSelector
import com.fpoly.project1.activity.checkout.CheckoutOverview
import com.fpoly.project1.activity.home.HomeFragment
import com.fpoly.project1.activity.product.ProductSearch
import com.google.android.material.bottomnavigation.BottomNavigationView

object MenuID {
    const val Home = 0
    const val Search = 1
    const val Checkout = 2
    const val Chat = 3
    const val Profile = 4
}

class MainActivity : AppCompatActivity() {
    private val tabsCount = 5
    private lateinit var viewPager: ViewPager

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.__main)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = PagerAdapter(supportFragmentManager, tabsCount)

        findViewById<BottomNavigationView>(R.id.home_bottom_navigation).setOnItemSelectedListener {
            println("${it.itemId}  - ${viewPager.currentItem}")

            when (it.itemId) {
                R.id.mHome -> {
                    viewPager.currentItem = MenuID.Home
                    return@setOnItemSelectedListener true
                }
                R.id.mSearch -> {
                    viewPager.currentItem = MenuID.Search
                    return@setOnItemSelectedListener true
                }
                R.id.mCheckout -> {
                    viewPager.currentItem = MenuID.Checkout
                    return@setOnItemSelectedListener true
                }
                R.id.mChat -> {
                    viewPager.currentItem = MenuID.Chat
                    return@setOnItemSelectedListener true
                }
                R.id.mProfile -> {
                    viewPager.currentItem = MenuID.Profile
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
    }
}

class PagerAdapter(fm: FragmentManager, private val tabsCount: Int) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return tabsCount
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            MenuID.Home -> HomeFragment()
            MenuID.Search -> ProductSearch()
            MenuID.Checkout -> CheckoutOverview()
            MenuID.Chat -> ChatSelector()
            MenuID.Profile -> AccountPanel()
            else -> HomeFragment()
        }
    }
}
