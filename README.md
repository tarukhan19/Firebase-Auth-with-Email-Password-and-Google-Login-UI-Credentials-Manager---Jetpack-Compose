🔐 Firebase Auth App
A modern Android authentication app built with Jetpack Compose using Firebase for email/password and Google Sign-In, integrated with Credential Manager for secure and seamless user login. Built following MVI architecture, this project ensures clean state management, separation of concerns, and scalable code.

🚀 Features
✅ Firebase Authentication

Sign up and login using email/password

Login with Google Sign-In

🔐 Credential Manager Integration

Save user credentials securely (email/password)

Auto-login using stored credentials

Smooth user experience with Google Identity Services

🗂 Session Persistence

Stores user login state using DataStore

Automatically navigates authenticated users to the home screen

🧩 Modern Android Tech Stack

Built using Jetpack Compose

Hilt for dependency injection

MVI architecture for clean and maintainable business logic

🛠 Tech Stack

Tool / Library	Purpose
Kotlin	Main programming language
Jetpack Compose	Declarative UI framework
Firebase Authentication	Backend for email/password and Google login
Google Credential Manager	Secure password storage and autofill
Hilt	Dependency injection
DataStore	Lightweight persistence for login state
MVI Architecture	Clean architecture pattern for UI logic
Coroutines & Flow	Asynchronous event handling and state flow
📱 Screens
Login Screen – Email/password login + Google Sign-In

Signup Screen – Create account with Firebase

Home Screen – Navigated to upon successful login

Credential Manager Prompt – Automatically fetch or save user credentials
