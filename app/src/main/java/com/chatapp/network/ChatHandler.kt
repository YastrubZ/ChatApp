package com.chatapp.network

import android.content.Context
import android.widget.Toast
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.Socket.*
import org.json.JSONObject

object ChatHandler {
    private const val SOCKET_IO_SERVER_ADDRESS = "http://185.13.90.140:8081"
    private const val JSON_KEY_USER = "user"
    private const val JSON_KEY_MESSAGE = "message"

    private val socket: Socket by lazy {
        IO.socket(SOCKET_IO_SERVER_ADDRESS, IO.Options())
    }

    private lateinit var currentUser: String
    private var chatListener: ChatListener? = null

    fun subscribe(
        currentUser: String,
        newUserMessage: String,
        chatListener: ChatListener,
        context: Context
    ){
        ChatHandler.currentUser = currentUser
        ChatHandler.chatListener = chatListener
        socket
            .on(EVENT_CONNECT) {
                chatListener.onNewMessage(newUserMessage)
            }
            .on(EVENT_MESSAGE) { result ->
                val userMessage = result.first() as JSONObject
                val user = userMessage[JSON_KEY_USER] as String
                if (currentUser != user){
                    val message = userMessage[JSON_KEY_MESSAGE] as String
                    chatListener.onNewMessage("$user: $message")
                }
            }
            .on(EVENT_CONNECT_ERROR) {
                Toast.makeText(context, (it[0] as Exception).message, Toast.LENGTH_SHORT).show()
            }
        socket.connect()
    }

    fun unsubscribe() {
        socket.disconnect()
        chatListener = null
    }

    fun sendMessage(user: String, message: String) {
        val messageJsonObject = JSONObject()
        messageJsonObject.put(JSON_KEY_USER, if (user.isEmpty()) currentUser else user)
        messageJsonObject.put(JSON_KEY_MESSAGE, message)
        socket.emit(EVENT_MESSAGE, messageJsonObject)
        chatListener?.onNewMessage(message, true)
    }
}