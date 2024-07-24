# Chatting App

Welcome to the Chatting App! This app allows users to communicate in real-time with each other and also integrate AI-driven responses using ChatGPT.

### Features

Real-time chat functionality. 

AI-powered chat responses using ChatGPT.

Firebase integration for secure and efficient data storage and management.

### ScreenShots
<table>
  <tr>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/de634b7c-32b4-4462-b218-989d37313341" width="350" />
    </td>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/0086885b-f850-4976-9fc5-115d66f67e7e" width="350" />
    </td>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/e0ae4e62-dbca-4fcf-895d-a4f209e47518" width="350" />
    </td>
  </tr>
  <tr>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/775e0554-c830-445f-93f6-21ecf02b7497" width="350" />
    </td>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/8770c1df-7916-490c-b0b9-b8326dbb6cc3" width="350" />
    </td>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/b0eb69f2-39d8-4503-9b94-11a304f97407" width="350" />
    </td>
  </tr>
  <tr>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/38746e71-e70f-4ac9-97e4-9588e62728c4" width="350" />
    </td>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/f3200335-dc93-4443-a319-ff1bf0998b92" width="350" />
    </td>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/637adc1e-e8a9-4206-ab0a-61f0a56619c6" width="350" />
    </td>
  </tr>
</table>





### Prerequisites

Android Studio,
Firebase account,
OpenAI account

# Installation
## Clone the repository:

git clone https://github.com/yourusername/your-repo.git

cd your-repo

### Open the project in Android Studio:

Open Android Studio and select File -> Open, then navigate to the project directory.

### Set up Firebase:

Go to the Firebase Console.

Create a new project or select an existing project.

Add an Android app to your project and follow the instructions to download the google-services.json file.

Place the google-services.json file in the app directory of your Android project.


### Set up Firebase Cloud Messaging (FCM):
bash
In the Firebase Console, navigate to Project settings -> Cloud Messaging.
Copy your Server key.


### Configure Firebase Server Key:

Open Constants.kt located in the com.example.chattingapp.model.
Add your Firebase server key to the SERVER_KEY constant.
kotlin
Copy code
package com.yourapp

object Constants {
    const val SERVER_KEY = "your-firebase-server-key-here"
}


Set up ChatGPT API:

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer your-chagpt-api-key-here")
            .post(body)
            .build()

        return client.newCall(request).execute()
    }
}


### Build and run the project:

Connect your Android device or start an emulator.
Click the Run button in Android Studio.
Usage
Sign up or log in to start chatting with other users.
To interact with the AI, simply type your message and the AI will respond.
