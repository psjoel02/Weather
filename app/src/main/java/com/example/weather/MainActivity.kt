package com.example.weather

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 2
        /*val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener { view ->

        }*/

        /* Inflate your custom layout
        val actionBarLayout = layoutInflater.inflate(R.layout.action_bar, null) as ViewGroup

        // Set up your ActionBar
        val actionBar: ActionBar = supportActionBar!!
        actionBar.setDisplayShowHomeEnabled(false)
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.customView = actionBarLayout
        // you can create listener over the EditText
        var actionBarText: EditText = findViewById(R.id.action_bar_text)
        //actionBarText.setText("hello world")*/

    }


}