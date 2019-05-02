package com.chatapp.network

interface ChatListener {
    fun onNewMessage(message: String, isCurrentUser: Boolean = false)
}