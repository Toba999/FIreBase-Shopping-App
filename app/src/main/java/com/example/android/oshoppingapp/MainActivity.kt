package com.example.android.oshoppingapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android.oshoppingapp.databinding.ActivityMainBinding
import com.example.android.oshoppingapp.utils.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.MYSHOPPAL_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val username=sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")!!

        binding.tvText.text="Hello $username "


    }
}