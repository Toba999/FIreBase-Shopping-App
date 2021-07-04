package com.example.android.oshoppingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.android.oshoppingapp.databinding.ActivitySplashBinding
@Suppress("DEPRECATION")

class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val decorView: View = window.decorView
        val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.setSystemUiVisibility(uiOptions)
        val actionBar: android.app.ActionBar? = actionBar
        actionBar?.hide()

        Handler().postDelayed(
            Runnable
            // Using handler with postDelayed called runnable run method
            {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                // close this activity
                finish()
            }, 3 * 1000
        )
    }
}