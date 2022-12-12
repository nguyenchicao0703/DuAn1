package com.fpoly.project1.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import com.fpoly.project1.activity.publish.PublishAddItem
import com.fpoly.project1.activity.publish.PublishOverview
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

object MenuID {
    const val Home = 0
    const val Search = 1
    const val Checkout = 2
    const val Publish = 3
    const val Chat = 4
    const val Profile = 5
}

class MainActivity : AppCompatActivity() {
    private val tabsCount = 6
    private lateinit var viewPager: ViewPager

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.__main)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = PagerAdapter(supportFragmentManager, tabsCount)

        findViewById<TabLayout>(R.id.home_bottom_navigation).let {
            it.setSelectedTabIndicatorColor(resources.getColor(R.color.accent))
            it.setupWithViewPager(viewPager)

            for (i in 0 until tabsCount) {
                val ico = ResourcesCompat.getDrawable(
                    resources,
                    when (i) {
                        MenuID.Home -> R.drawable.ic_home
                        MenuID.Search -> R.drawable.ic_search
                        MenuID.Checkout -> R.drawable.ic_add_cart
                        MenuID.Publish -> R.drawable.ic_publish
                        MenuID.Chat -> R.drawable.ic_chat
                        MenuID.Profile -> R.drawable.ic_person
                        else -> R.drawable.ic_home
                    },
                    null
                )
                ico?.let { draw ->
                    draw.setTint(resources.getColor(R.color.accent))

                    val iv = ImageView(this)
                    iv.setImageDrawable(ico)

                    it.getTabAt(i)?.customView = iv
                }
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
            MenuID.Publish -> PublishOverview()
            MenuID.Chat -> ChatSelector()
            MenuID.Profile -> AccountPanel()
            else -> HomeFragment()
        }
    }
}
