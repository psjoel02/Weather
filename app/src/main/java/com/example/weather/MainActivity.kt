package com.example.weather

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.viewpager.widget.ViewPager
import com.example.weather.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.example.weather.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var tab: TabLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Weather_NoActionBar)
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        tab = findViewById(R.id.tabs)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 2
        if(Build.VERSION.SDK_INT <= 30){
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        else{
            tabs.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(0,130,0,0)
            }
        }

    }


}