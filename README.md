# MyDailyLuck 🍀

A motivational Android app that gives you a daily “Luck Score” based on your guess of a random number (1–100).

---

## 🚀 Features

- **Daily Luck Test**  
  Guess a number between 1 and 100. The closer you are to the hidden random value, the better your score.
- **Daily Notification**  
  Receives a push notification every morning (8–10 AM) to remind you to check your luck.  
- **BootReceiver**  
  Automatically reschedules the notification after device reboot.  
- **Integrity Checks**  
  Detects rooted devices and verifies APK signature to prevent tampering.  
- **Modern UI**  
  Built entirely with Jetpack Compose for a clean, reactive design.  
- **Usage Timer**  
  Limits you to one attempt per day and shows countdown until next available session.  
- **In-App Legal**  
  Includes Info screen with Imprint, Privacy Policy and Terms of Use (rendered from Markdown).

---

## 🛠️ Technology Stack

- **Kotlin** & **Jetpack Compose**  
- **WorkManager** for scheduling notifications  
- **BroadcastReceiver** (`BootReceiver`) for reboot handling  
- **SharedPreferences** for persisting user data  
- **Gradle Kotlin DSL** (`.kts`)  
- **Min SDK**: 21 (Android 5.0), Target SDK: 31+

---

## 🤝 Feedback

This project is not open for public contributions, but feedback and suggestions are welcome via GitHub Issues.

---

## 🔧 Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer  
- Android SDK Platform 31  
- Java Development Kit (JDK) 11+

### Installation

```bash
# Clone this repository
git clone https://github.com/lqN5M2Xy/MyDailyLuck.git

# Open in Android Studio
cd MyDailyLuck
# In Android Studio: File → Open → select this folder

# Let Android Studio sync Gradle and download dependencies
