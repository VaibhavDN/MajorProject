@file:Suppress("DEPRECATION")

package com.prabalbhavishya.cars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import androidx.viewpager.widget.ViewPager

import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewpager = findViewById<ViewPager>(R.id.home_screen_viewpager)
        val pageAdapter = ViewPagerAdapter(supportFragmentManager)
        viewpager.adapter = pageAdapter
        viewpager.setCurrentItem(1, false)
        val mFlingDistance: Field
        mFlingDistance = ViewPager::class.java.getDeclaredField("mFlingDistance")
        mFlingDistance.isAccessible = true
        mFlingDistance.set(viewpager, 30)
    }

    class ViewPagerAdapter(fm: FragmentManager) :
            FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            when (position) {
                2 -> return SettingsFragment() //Settings fragment at position 2
                1 -> return HomeFragment() //Homefrag at position 0
                0 -> return LeftFragment() //Leftfrag at position 1
            }
            return HomeFragment() //does not happen
        }

        override fun getCount(): Int {
            return 3 //three fragments
        }
    }
}