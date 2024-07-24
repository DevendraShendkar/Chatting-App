import com.google.firebase.database.PropertyName
import com.google.firebase.database.ServerValue
import com.google.firebase.messaging.reporting.MessagingClientEvent

class Message {
    var messageId: String? = null
    @get:PropertyName("message")
    var message: String? = null
    var senderId: String? = null
    var receiverId: String? = null
    var timestamp: Any? = null
    var image: String? = null
    var type: String? = null
    var replayId: String? = null
    var repliedPosition: String? = null

    constructor(){}

    constructor(messageId: String? = null, message: String? = null, senderId: String? = null,receiverId: String? = null,
                timestamp: Any? = null, image: String? = null, type: String? = null,replayId:String? = null, repliedPosition: String? = null) {
        this.messageId = messageId
        this.message = message
        this.senderId = senderId
        this.receiverId = receiverId
        this.timestamp = timestamp
        this.image = image
        this.type = type
        this.replayId = replayId
        this.repliedPosition = repliedPosition

    }
}
