package com.example.chattingapp.onBoarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.chattingapp.R
import com.example.chattingapp.onBoarding.screens.AFragment
import com.example.chattingapp.onBoarding.screens.BFragment
import com.example.chattingapp.onBoarding.screens.CFragment

class ViewPagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)

        val fragmentList = arrayListOf<Fragment>(
            AFragment(),
            BFragment(),
            CFragment()

        )
        val adapter = ViewPagerAdapter(
            fragmentList,
            this.supportFragmentManager,
            lifecycle
        )
        findViewById<ViewPager2>(R.id.viewPager).adapter = adapter
    }
}