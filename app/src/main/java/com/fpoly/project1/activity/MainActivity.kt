package com.fpoly.project1.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fpoly.project1.R
import com.fpoly.project1.activity.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = PagerAdapter(supportFragmentManager, 4)

        (findViewById<View>(R.id.home_bottom_navigation) as BottomNavigationView).setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.mHome -> {
                    viewPager.currentItem = 0
                    return@setOnItemSelectedListener true
                }
                R.id.mChat -> {
                    viewPager.currentItem = 1
                    return@setOnItemSelectedListener true
                }
                R.id.mOrder -> {
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

internal class PagerAdapter(fm: FragmentManager?, private val tabsCount: Int) :
    FragmentStatePagerAdapter(
        fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    override fun getCount(): Int {
        return tabsCount
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) HomeFragment() else TODO("Add other fragments")
    }
}