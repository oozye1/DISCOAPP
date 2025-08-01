package com.example.discoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.discoapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 * The main activity hosts a [ViewPager2] with two fragments: one for disco lighting and one for
 * audio visualization. A tab layout in the app bar allows the user to switch between modes
 * seamlessly. The page titles and icons are defined in [SectionsPagerAdapter].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Set up the ViewPager with the sections adapter.
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter

        // Attach the TabLayout to the ViewPager and set the titles/icons.
        val tabTitles = listOf(
            getString(R.string.disco_mode),
            getString(R.string.visualizer_mode)
        )
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}