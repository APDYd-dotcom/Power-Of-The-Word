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
- Seamless playback using **Media3 ExoPlayer**.

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
- **Device Tracking:** Unique device-based analytics for views and engagement.

---

## 🛠 Tech Stack

- **UI:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM
- **Dependency Injection:** Hilt
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
├── di            # Hilt dependency injection modules
├── service       # Firebase Messaging services
├── ui
│   ├── navigation # Compose destination routing
│   ├── screens    # Feature-specific screens (Home, Video, Radio, etc.)
│   └── theme      # Material 3 Theme definitions (Color, Shape, Type)
└── util          # Helper classes (Sharing, Device Identification)
```

---

## 📜 License

This project is developed for the **Power of the Word Ministry**. All rights reserved.

---

## 👨‍💻 Author
Built with ❤️ by the Power of the Word Development Team.

pip install google-api-python-client

from googleapiclient.discovery import build
import isodate # Optional: to parse ISO 8601 duration (pip install isodate)

YOUTUBE_API_KEY = 'YOUR_API_KEY_HERE'
youtube = build('youtube', 'v3', developerKey=YOUTUBE_API_KEY)

def get_youtube_video_details(video_id):
# 1. Fetch Video Details (Duration)
video_response = youtube.videos().list(
part='contentDetails,snippet',
id=video_id
).execute()

    if not video_response['items']:
        return None

    video_item = video_response['items'][0]
    duration_iso = video_item['contentDetails']['duration'] # e.g., PT12M30S
    channel_id = video_item['snippet']['channelId']

    # 2. Fetch Channel Details (Logo)
    channel_response = youtube.channels().list(
        part='snippet',
        id=channel_id
    ).execute()

    channel_logo = channel_response['items'][0]['snippet']['thumbnails']['default']['url']

    return {
        'duration': duration_iso, # You can parse this to '12:30'
        'channel_logo': channel_logo
    }



class VideoSerializer(serializers.ModelSerializer):
duration = serializers.SerializerMethodField()
channel_logo = serializers.SerializerMethodField()

    class Meta:
        model = Video
        fields = ['id', 'title', 'url', 'duration', 'channel_logo', ...]

    def get_duration(self, obj):
        # Implementation to call utility and cache result
        return "12:30" 

    def get_channel_logo(self, obj):
        return "https://yt3.ggpht.com/..."