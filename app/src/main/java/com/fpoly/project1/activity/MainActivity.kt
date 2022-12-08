package com.fpoly.project1.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fpoly.project1.R
import com.fpoly.project1.activity.account.AccountPanel
import com.fpoly.project1.activity.chat.ChatSelector
import com.fpoly.project1.activity.home.HomeFragment
import com.fpoly.project1.activity.product.ProductSearch
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val tabsCount = 4
    lateinit var viewPager: ViewPager

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
                    viewPager.currentItem = 0
                    return@setOnItemSelectedListener true
                }
                R.id.mOrder -> {
                    viewPager.currentItem = 1
                    return@setOnItemSelectedListener true
                }
                R.id.mChat -> {
                    viewPager.currentItem = 2
                    return@setOnItemSelectedListener true
                }
                R.id.mProfile -> {
                    viewPager.currentItem = 3
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
            0 -> HomeFragment()
            1 -> ProductSearch()
            2 -> ChatSelector()
            3 -> AccountPanel()
            else -> HomeFragment()
        }
    }
}
