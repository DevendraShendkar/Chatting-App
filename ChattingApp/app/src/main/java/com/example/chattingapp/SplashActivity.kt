package com.example.chattingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.chattingapp.databinding.ActivitySplashBinding
import com.example.chattingapp.onBoarding.ViewPagerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.favWhite)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.favWhite)

        Handler().postDelayed({
            if (onBoardingFinished()) {
                if(loggedIn()) {
                    val intent = Intent(this@SplashActivity, ChatViewActivity::class.java)
                    FirebaseMessaging.getInstance().getToken(/* forceRefresh= */)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result

                                val databaseReference =
                                    FirebaseDatabase.getInstance().getReference("user")
                                        .child(FirebaseAuth.getInstance().uid!!)
                                Log.d("tokenhere",token.toString())

                                val map: MutableMap<String, Any> = HashMap()
                                map["token"] = token!!
                                databaseReference.updateChildren(map)
                            }
                            FirebaseMessaging.getInstance().subscribeToTopic("message")
                            startActivity(intent)
                            finish()
                        }
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val i = Intent(this@SplashActivity, ViewPagerActivity::class.java)
                startActivity(i)
                finish()
            }
        }, 1500)
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = this.getSharedPreferences("onBoarding", MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    private fun loggedIn(): Boolean {
        val sharedPref = this.getSharedPreferences("CHECK", MODE_PRIVATE)
        return sharedPref.getBoolean("LOGIN", false)

    }
}