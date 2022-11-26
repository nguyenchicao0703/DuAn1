package com.fpoly.project1.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fpoly.project1.R;
import com.fpoly.project1.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), 4));

        ((BottomNavigationView) findViewById(R.id.home_bottom_navigation)).setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mHome:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.mChat:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.mOrder:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.mProfile:
                    viewPager.setCurrentItem(3);
                    return true;
                default:
                    return false;
            }
        });
    }
}


class PagerAdapter extends FragmentStatePagerAdapter {
    private final int tabsCount;

    public PagerAdapter(FragmentManager fm, int tabsCount) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.tabsCount = tabsCount;
    }

    public int getCount() {
        return this.tabsCount;
    }

    public Fragment getItem(int position) {
        return position == 0 ? new HomeFragment() : null; // TODO add fragments here
    }
}
