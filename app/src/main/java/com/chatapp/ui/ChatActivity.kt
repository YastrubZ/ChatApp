package com.chatapp.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.chatapp.R
import com.chatapp.network.ChatHandler
import com.chatapp.network.ChatListener
import androidx.core.view.setPadding
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity(), ChatListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        ChatHandler.subscribe(
            resources.getString(R.string.default_user_name),
            resources.getString(R.string.default_user_message),
            this,
            baseContext
        )

        sendButton.setOnClickListener {
            sendMessage()
        }

        messageEditText.doAfterTextChanged{
            sendButton.isEnabled = it!!.isNotEmpty()
        }
    }

    override fun onDestroy() {
        ChatHandler.unsubscribe()
        super.onDestroy()
    }

    private fun sendMessage() {
        ChatHandler.sendMessage(
            userEditText.text.toString(),
            messageEditText.text.toString()
        )
        messageEditText.text.clear()
    }

    override fun onNewMessage(message: String, isCurrentUser: Boolean) {
        runOnUiThread {
            val newTextView = TextView(this)
            newTextView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            newTextView.gravity = if(isCurrentUser) Gravity.END else Gravity.START
            newTextView.text = message
            newTextView.setPadding(resources.getDimension(R.dimen.default_text_padding).toInt())
            chatLayout.addView(newTextView)
            scrollView.postDelayed({ scrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 100)
        }
    }
}
