package com.example.chattingapp.model

import com.google.firebase.database.ServerValue

class User {
    var email: String? = null
    var userName: String? = null
    var uid: String? = null
    var name: String? = null
    var age: Int? = null
    var profileImageUrl: String? = null
        get() = field
        set(value) {
            field = value
        }
    var city: String? = null
    var phoneNo: String? = null
    var status: Boolean? = null
    var bio: String? = null
    var lastChatTimestamp: Any? = null
    var token: String? = null
    var latestSeenTimeStamp: Any? = null
    var backgroundDiagonal: String? = null
    var birthday: String? = null

    constructor() {}

    constructor(
        userName: String? =null,
        email: String? = null,
        uid: String?,
        name: String? = null,
        age: Int? = null,
        profileImageUrl: String? = null,
        city: String? = null,
        phoneNo: String? = null,
        status: Boolean? = null,
        bio:String? =null,
        lastChatTimestamp:Any? = ServerValue.TIMESTAMP,
        token:String? = null,
        backgroundDiagonal:String? = null,
        birthday: String? = null

    ) {
        this.userName = userName
        this.email = email
        this.uid = uid
        this.name = name
        this.age = age
        this.profileImageUrl = profileImageUrl
        this.city = city
        this.phoneNo = phoneNo
        this.status = status
        this.bio = bio
        this.lastChatTimestamp =lastChatTimestamp
        this.token = token
        this.backgroundDiagonal = backgroundDiagonal
        this.birthday = birthday

    }
}
