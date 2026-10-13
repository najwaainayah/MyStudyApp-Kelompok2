package com.example.mystudyapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val durasiSplashMs = 1500L   // 1.5 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // pengaturan (bahasa & tema) sudah dibaca di MyStudyApp.onCreate()
        // sebelum Activity manapun dibuat, jadi splash ini murni jeda tampilan
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, durasiSplashMs)
    }
}