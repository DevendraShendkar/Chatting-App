package com.example.chattingapp.model

class Group {
    var groupName: String? = null
    var groupCreateId: String? = null
    var groupId: String? = null
    var groupImage: String? = null
    var timestamp: Any? = null
    var imageMessage: String? = null
    var type: String? = null
    var message: String? = null
    var messageSenderId: String? = null


    constructor() {}

    constructor(groupId: String? = null, groupCreateId: String? = null, groupName: String? = null, groupImage: String? = null,
                timestamp: Any? = null, imageMessage: String? = null, type: String? = null,message: String? = null, messageSenderId: String? = null
    ) {
        this.groupId = groupId
        this.groupCreateId = groupCreateId
        this.groupName = groupName
        this.groupImage = groupImage
        this.timestamp = timestamp
        this.imageMessage = imageMessage
        this.type = type
        this.message = message
        this.messageSenderId = messageSenderId
    }
}
