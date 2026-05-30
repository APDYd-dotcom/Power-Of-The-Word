# Power of the Word – Mobile Application

A professional Android application built with **Jetpack Compose** and **Kotlin**, designed to provide church members with seamless access to sermons, live broadcasts, radio sessions, and daily spiritual encouragement.

---

## 📱 Key Features

### 🎬 Video Sermons
- Full YouTube integration using the `android-youtube-player` library.
- Categorized by type: **Preach, Testimony, Live**.
- Multi-language support (English, French, Swahili, Kirundi).
- View tracking and interaction (Likes/Shares).

### 📻 Church Radio & Audio
- Live Radio broadcasting with automatic activation based on schedule.
- Daily Audio messages (ISEGERO) with search by date.
- **Audio Downloads:** Save audio sessions for offline listening with a dedicated download manager.
- Seamless playback using **Media3 ExoPlayer**.

### 💰 Support & Donations
- Integrated donation system to support the ministry's mission and outreach programs.

### ✨ Daily Word & Immersive UI
- **Immersive Header:** Dynamic "Daily Word" header that adapts to system themes and provides a clean, modern aesthetic.
- **Dark & Light Mode:** Full Material 3 support for adaptive theme switching.
- **Pull-to-Refresh:** Easy content updates using modern Compose components.

### 📅 Church Management
- **Programs:** Complete weekly schedule of services and meetings.
- **Horaire:** Direct access to pastoral availability and prayer line contacts.
- **Feeds:** Real-time church news, announcements, and special events.

### 🔔 Notifications & Engagement
- **Firebase FCM:** Automatic push notifications for new uploads, live streams, and broadcasts.
- **Social Sharing:** Robust sharing utilities for spreading the Word across various platforms.
- **Device Tracking:** Unique device-based analytics for views and engagement.

---

## 🛠 Tech Stack

- **UI:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM
- **Dependency Injection:** Koin
- **Networking:** Ktor Client (CIO)
- **Media:** Media3 ExoPlayer & Android YouTube Player
- **Image Loading:** Coil
- **Local Storage:** DataStore Preferences
- **Backend:** Django REST Framework (hosted at `power.clubtechlac.bi`)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17+
- Gradle 8.10.2

### Build & Run
1. Clone the repository.
2. Ensure you have a valid `google-services.json` in the `app/` folder for Firebase features.
3. Sync project with Gradle files.
4. Run the `:app:assembleDebug` task.

---

## 🏗 Project Structure

```
com.poweroftheword.poweroftheword
│
├── data          # Repository implementations and API logic
├── domain        # Business models and repository interfaces
├── di            # Koin dependency injection modules
├── service       # Firebase Messaging services
├── ui
│   ├── navigation # Compose destination routing
│   ├── screens    # Feature-specific screens (Home, Video, Radio, etc.)
│   └── theme      # Material 3 Theme definitions (Color, Shape, Type)
└── util          # Helper classes (Sharing, Download Manager, Device Identification)
```

---

## 📜 License

This project is developed for the **Power of the Word Ministry**. All rights reserved.

---

## 👨‍💻 Author
Built with ❤️ by the Power of the Word Development Team.
