package com.example.chat.chatGPT

class MessageGpt {
    companion object {
        const val SENT_BY_ME = "me"
        const val SENT_BY_BOT = "bot"
    }

    var message: String? = null
        @JvmName("getMessageProperty")
        get() = field
        @JvmName("setMessageProperty")
        set(value) {
            field = value
        }

    var sentBy: String? = null
        @JvmName("getSentByProperty")
        get() = field
        @JvmName("setSentByProperty")
        set(value) {
            field = value
        }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getSentBy(): String? {
        return sentBy
    }

    fun setSentBy(sentBy: String?) {
        this.sentBy = sentBy
    }
    constructor(){}

    constructor(message: String?, sentBy: String?) {
        this.message = message
        this.sentBy = sentBy
    }
}