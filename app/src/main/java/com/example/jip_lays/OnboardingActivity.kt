package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.jip_lays.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnboardingPagerAdapter(this)
        binding.viewPagerOnboarding.adapter = adapter

        setupDotsIndicator(adapter.itemCount, 0)

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvPageIndicator.text = "Halaman ${position + 1} dari ${adapter.itemCount}"
                setupDotsIndicator(adapter.itemCount, position)
                if (position == adapter.itemCount - 1) {
                    binding.btnNext.text = "Mulai Aplikasi"
                } else {
                    binding.btnNext.text = "Selanjutnya"
                }
            }
        })

        binding.btnSkip.setOnClickListener {
            completeOnboarding()
        }

        binding.btnNext.setOnClickListener {
            val current = binding.viewPagerOnboarding.currentItem
            if (current < adapter.itemCount - 1) {
                binding.viewPagerOnboarding.currentItem = current + 1
            } else {
                completeOnboarding()
            }
        }
    }

    private fun setupDotsIndicator(count: Int, activePosition: Int) {
        binding.layoutDots.removeAllViews()
        val dots = arrayOfNulls<ImageView>(count)
        for (i in 0 until count) {
            dots[i] = ImageView(this).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnboardingActivity,
                        if (i == activePosition) R.drawable.ic_dot_active else R.drawable.ic_dot_inactive
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                layoutParams = params
            }
            binding.layoutDots.addView(dots[i])
        }
    }

    private fun completeOnboarding() {
        // Save onboarding completion status using separate key
        val sharedPref = getSharedPreferences("app_pref", MODE_PRIVATE)
        sharedPref.edit().putBoolean("isOnboardingCompleted", true).apply()

        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
