package com.example.whatsappclone.listener

interface ChatsClickListener {
    fun onChatClicked(
        chatId: String?,
        otherUserId: String?,
        chatsImageUrl:String?,
        chatsName:String?
    )
}