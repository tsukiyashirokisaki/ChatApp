# ChatApp
Target device: Pixel (1080*1920, 420dpi)
Develop Tools: Android Studio (Windows 64-bit)
Progamming Language: Java
## Implemented Features
* Scrollable chat messages (required)
* Messages are displayed from bottom to top in decending order of time (required)
* Fixed input row (required)
* EditTextView and Send Button are in the input row (required)
* Offline accessing to data is enabled by Firebase disk persistence (bonus)
* "Login", "Register", and "Reset Password" are enabled by Firebase Authentication (bonus)
* Read/Write user's information in Firebase Realtime Database (bonus)
* Read/Write user's profile image in Firebase Storage
* Record the sending time of the message and show it in the chatroom
* Adjust chatroom interface when the keyboard is popped up
* Remember the password after logging in at the first time
## Important Packages
````
com.github.bumptech.glide:glide:4.11.0
de.hdodenhof:circleimageview:3.1.0
io.paperdb:paperdb:2.7.1
com.google.firebase:firebase-database:19.7.0
com.google.firebase:firebase-auth:20.0.3
com.google.firebase:firebase-storage:19.2.2
````
## TODO
* Pop up notification when messages come
* Allow user to send pictures, voice, or other types of data
* Allow user to retract their messages
* Implement chatbot in back-end
## Demo Video
[![](http://img.youtube.com/vi/CugO6Cd5vpY/0.jpg)](http://www.youtube.com/watch?v=CugO6Cd5vpY "")
## Testing
Three accounts with password="123456" are created for testing:
* b06507002@ntu.edu.tw
* b06507002@g.ntu.edu.tw
* bokuwa08201230@gmail.com
