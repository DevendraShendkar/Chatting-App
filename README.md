# Chatting App

Welcome to the Chatting App! This app allows users to communicate in real-time with each other and also integrate AI-driven responses using ChatGPT.

### Features

Real-time chat functionality. 

AI-powered chat responses using ChatGPT.

Firebase integration for secure and efficient data storage and management.

### Prerequisites

Android Studio,
Firebase account,
OpenAI account

# Installation
## Clone the repository:

Copy code
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

In the Firebase Console, navigate to Project settings -> Cloud Messaging.
Copy your Server key.


### Configure Firebase Server Key:

Open Constants.kt located in the com.yourapp package.
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
