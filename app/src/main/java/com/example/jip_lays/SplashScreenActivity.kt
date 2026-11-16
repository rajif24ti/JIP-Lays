package com.example.jip_lays

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {
            delay(2000)

            val appPref = getSharedPreferences("app_pref", MODE_PRIVATE)
            val isOnboardingCompleted = appPref.getBoolean("isOnboardingCompleted", false)

            val userPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val isLogin = userPref.getBoolean("isLogin", false)

            val intentTarget = when {
                !isOnboardingCompleted -> OnboardingActivity::class.java
                isLogin -> BaseActivity::class.java
                else -> LoginActivity::class.java
            }

            startActivity(Intent(this@SplashScreenActivity, intentTarget))
            finish()
        }
    }
}
