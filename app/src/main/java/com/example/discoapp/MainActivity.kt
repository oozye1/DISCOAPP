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

    override fun onStart() {
        super.onStart()
        // Setup menu button to show/hide the box of 50 tiny buttons
        val menuButton = binding.menuButton
        val buttonBoxScroll = binding.buttonBoxScroll
        val buttonBox = binding.buttonBox

        // Only add buttons once
        if (buttonBox.childCount == 0) {
            for (i in 1..50) {
                val btn = android.widget.Button(this).apply {
                    text = i.toString()
                    textSize = 10f
                    minWidth = 0
                    minHeight = 0
                    setPadding(4, 4, 4, 4)
                    layoutParams = android.widget.GridLayout.LayoutParams().apply {
                        width = 0
                        height = android.widget.GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f)
                        setMargins(2, 2, 2, 2)
                    }
                    setOnClickListener {
                        val effectIndex = i - 1
                        val currentItem = binding.viewPager.currentItem
                        val fragment = supportFragmentManager.findFragmentByTag("f$currentItem")
                        when (fragment) {
                            is DiscoFragment -> fragment.setEffectByIndex(effectIndex)
                            is VisualizerFragment -> fragment.setEffectByIndex(effectIndex)
                        }
                    }
                }
                buttonBox.addView(btn)
            }
        }

        menuButton.setOnClickListener {
            buttonBoxScroll.visibility = if (buttonBoxScroll.visibility == android.view.View.VISIBLE) android.view.View.GONE else android.view.View.VISIBLE
        }
    }
}
