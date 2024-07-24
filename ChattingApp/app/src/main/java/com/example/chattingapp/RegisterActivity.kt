package com.example.chattingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.chattingapp.databinding.ActivityRegisterBinding
import com.example.chattingapp.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.AccountPicker
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class RegisterActivity : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityRegisterBinding

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.gcm_defaultSenderId))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.textViewGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.buttonSignUp.setOnClickListener {
            if (binding.editTextUsername.text.isEmpty()) {
                Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT).show()
            } else if (binding.editTextEmail.text.isEmpty()) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            } else if (binding.editTextPassword.text.isEmpty())
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
            else if (binding.editTextPassword.text.toString() != binding.editTextConfirmPassword.text.toString())
                Toast.makeText(this, "Password Not Matched", Toast.LENGTH_SHORT).show()
            else {
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val userName = binding.editTextUsername.text.toString()

                binding.lottie.isVisible = true
                binding.buttonSignUp.isVisible = false
                binding.textViewGoogle.isVisible = false
                signUp(userName, email, password)
            }
        }
    }

    private fun signUp(userName: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Registration successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    val sharedPref = this.getSharedPreferences("CHECK", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("EMAIL_KEY", email)
                    editor.putString("PASS_KEY", password)
                    editor.putBoolean("LOGIN", true)
                    editor.apply()

                    val user = User(userName, email, mAuth.uid)
                    addUserToDatabase(user)
                    val intent = Intent(this@RegisterActivity, SetProfile::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

                } else {
                    binding.lottie.isVisible = false
                    binding.buttonSignUp.isVisible = true
                    binding.textViewGoogle.isVisible = true

                    Toast.makeText(applicationContext, "Registration failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun addUserToDatabase(user: User) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(user.uid!!).setValue(user)
        addToken()
    }


    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        val accountPickerIntent = AccountPicker.newChooseAccountIntent(
            null, null,
            arrayOf("com.google"), true, null, null, null, null
        )
        val intentArray = arrayOf(signInIntent, accountPickerIntent)
        val chooserIntent = Intent.createChooser(intentArray[0], "Select an account")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        launcher.launch(chooserIntent)
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val database = FirebaseDatabase.getInstance().reference.child("user")

                val query = database.orderByChild("email").equalTo(account.email)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            val user = snapshot.children.first().getValue(User::class.java)
                            val intent = Intent(applicationContext, HomeMain::class.java)
                            intent.putExtra("email", user?.email)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("name", user?.name)

                            val sharedPref = applicationContext.getSharedPreferences(
                                "CHECK",
                                Context.MODE_PRIVATE
                            )
                            val editor = sharedPref.edit()
                            editor.putBoolean("LOGIN", true)
                            editor.putString("LoginSpe", user?.email)
                            editor.apply()
                            startActivity(intent)
                            finish()
                        } else {

                            val intent = Intent(applicationContext, SetProfile::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("email", account.email)
                            intent.putExtra("name", account.displayName)
                            Toast.makeText(
                                applicationContext,
                                "Registration successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            val sharedPref = applicationContext.getSharedPreferences(
                                "CHECK",
                                Context.MODE_PRIVATE
                            )
                            val editor = sharedPref.edit()
                            editor.putBoolean("LOGIN", true)
                            editor.apply()

                            val user = User(
                                account.displayName,
                                account.email,
                                mAuth.uid,
                                account.displayName
                            )
                            addUserToDatabase(user)
                            startActivity(intent)
                            finish()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Database error: $error")
                    }
                })

            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun addToken() {
        FirebaseMessaging.getInstance().getToken(/* forceRefresh= */)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result

                    val databaseReference =
                        FirebaseDatabase.getInstance().getReference("user")
                            .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                    Log.d("tokenhere", token.toString())

                    val map: MutableMap<String, Any> = HashMap()
                    map["token"] = token!!
                    databaseReference.updateChildren(map)
                }
                FirebaseMessaging.getInstance().subscribeToTopic("message")
            }
    }
}