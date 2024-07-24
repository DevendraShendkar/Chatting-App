package com.example.chattingapp.chatGPT

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.chatGPT.MessageGpt
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ActivityChatGptBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ChatGPT : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageListGpt: MutableList<MessageGpt>
    private lateinit var messageAdapter: MessageAdapterChatGpt
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    private lateinit var binding: ActivityChatGptBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatGptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

        messageListGpt = mutableListOf()

        recyclerView = findViewById(R.id.recycler_view)

        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_btn)

        // setup recycler view
        messageAdapter = MessageAdapterChatGpt(messageListGpt)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        sendButton.setOnClickListener {
            val question = messageEditText.text.toString().trim()
            addToChat(question, MessageGpt.SENT_BY_ME)
            messageEditText.text.clear()
            callAPI(question)
            binding.robotHi.visibility = View.GONE
        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageListGpt.add(MessageGpt(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        messageListGpt.removeAt(messageListGpt.size - 1)
        addToChat(response, MessageGpt.SENT_BY_BOT)
    }

    private fun callAPI(question: String) {
        messageListGpt.add(MessageGpt("Typing... ", MessageGpt.SENT_BY_BOT))

        val jsonBody = JSONObject().apply {
            put("model", "text-davinci-003")
            put("prompt", question)
            put("max_tokens", 4000)
            put("temperature", 0)
        }

        val body = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "") // in header put chatgpt api key
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response due to " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.body!!.string())
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")
                        addResponse(result.trim())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body?.string())
                }
                response.close()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        updateUserStatus(true)
    }

    override fun onPause() {
        super.onPause()
        updateUserStatus(false)
    }

    private fun updateUserStatus(status: Boolean) {
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        val firebaseDef = FirebaseDatabase.getInstance().getReference("user/$uid")
        firebaseDef.child("status").setValue(status)
    }
}
