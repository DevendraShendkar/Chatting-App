package com.example.chattingapp

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.chattingapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

        mAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener() {
            if (binding.email.text.isEmpty())
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            else if (binding.password.text.isEmpty())
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
            else {
                email = binding.email.text.toString()
                password = binding.password.text.toString()
                logIn(email, password)
            }
        }
        binding.signupText.setOnClickListener() {
            val i = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(i)
        }
    }

    private fun logIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, ChatViewActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    val sharedPref = this.getSharedPreferences("CHECK", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putBoolean("LOGIN",true)
                    editor.apply()

                } else {
                    Log.e("LoginFragment", "Login failed: ${task.exception?.message}")
                    Toast.makeText(this@LoginActivity,"User Doesn't Exists", Toast.LENGTH_SHORT).show()

                }
            }


    }

}