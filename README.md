POWER OF THE WORLD – Mobile Application
User Manual & Functional Overview  
1. Introduction
POWER OF THE WORLD is a mobile application designed to help church members and followers stay connected with the ministry anytime and anywhere.
The application provides access to sermons, live broadcasts, radio sessions, church programs, daily spiritual messages, and important announcements. It allows users to engage with the church through multimedia content and stay informed about upcoming activities.
The app is designed to be simple, accessible, and multilingual so believers from different backgrounds can easily use it.

2. Main Features of the Application
2.1 Video Sermons
The application provides a library of church videos hosted on YouTube.
Users can:
Watch sermons and testimonies directly from the app
Search videos by title
View videos according to their selected language
Like videos they appreciate
Share videos with friends and family
Track the number of views
This allows believers to learn, grow spiritually, and share the message easily.

2.2 Live Streaming
The app includes Live Broadcast functionality.
When the church starts a live broadcast:
Users will receive a notification
They can watch the live service directly in the app
The live stream is powered by a secure streaming system
This feature allows followers to participate in services even when they cannot attend physically.

2.3 Church Radio
The application includes a Radio feature.
When the pastor has a radio program:
The radio will automatically become available in the app
Users can listen live during the broadcast time
This ensures believers never miss important teachings and discussions.

2.4 Daily Audio Messages
Every day, the pastor can upload audio teachings or spiritual messages.
Users can:
Listen to daily audio messages
Search audio by date
Like the audio messages
Share them with others
This feature encourages daily spiritual growth through listening.

2.5 Daily Word
The app displays Daily Words of encouragement and scripture.
Each day, users will receive:
A spiritual message
A scripture image
Inspiration for their daily life
This keeps believers spiritually motivated every day.

2.6 Church Programs (Weekly Schedule)
The Program section displays the weekly schedule of church services.
Users can see:
Service name
Description of the event
Day of the week
Start and end time
This helps members plan their participation in church activities.

2.7 Pastor Availability (Horaire)
The Horaire section shows when the pastor is available.
Users can see:
Pastor's name
Contact number
Day and time availability
Language spoken
This allows members to schedule communication or consultation with the pastor.

2.8 Church News & Events (Feeds)
The Feeds section displays announcements and updates about church activities.
Examples include:
Upcoming church events
Special gatherings
Conferences
Community activities
These updates may include images and descriptions.

2.9 Donation System
The application includes a donation option.
Users can:
Support the ministry financially
Contribute to church projects
Participate in offerings through secure payment
This allows members and supporters to support the mission of the church easily.

2.10 Social Engagement
The app allows users to interact with content.
They can:
Like videos and audio messages
Share sermons with others
View popular teachings
This helps spread the message to a wider audience.

3. Multi-Language Support
The application supports four languages:
English
French
Swahili
Kirundi
When a user installs the application, they can choose their preferred language.
After selecting a language:
All videos
Audio messages
Church information
Menu items
will be displayed only in the selected language.
This ensures the app is accessible to everyone in the community.

4. Notifications
Users will receive notifications when:
A new video is uploaded
A live broadcast starts
A new audio message is available
Important church announcements are published
This helps users stay connected with church activities in real time.

5. Benefits of the Application
The POWER OF THE WORLD application will help the church to:
Reach members who cannot attend physically
Spread sermons and teachings globally
Share daily spiritual encouragement
Communicate church programs easily
Strengthen engagement within the church community
The application becomes a digital platform for the ministry.


6. OFFICIAL WEBSITE FUNCTIONALITY
• The church will have an official website that presents general information about the ministry.
The website will include:
Church Presentation
Overview of the church
History of the ministry
Vision and Mission of the church
Leadership Information
Information about the pastor
Introduction of church leadership
Popular Sermons
Display of selected popular videos from the ministry
Videos will be embedded from YouTube
Visitors can watch sermons directly from the website
Church Information
Contact information
Church location
Social media links
Purpose of the Website
The website serves as the public presentation platform of the church, allowing visitors and new members to learn about the ministry and its mission.
The website is not intended to replace the mobile application, but to introduce the church and provide limited media content.

7. ADMIN DASHBOARD FUNCTIONALITY
The system will include an Admin Dashboard used by church administrators to manage all application content.
Through the dashboard, administrators will be able to:
Content Management
Upload and manage videos
Upload daily audio messages
Start and manage live broadcasts
Manage radio broadcast schedules
Church Communication
Publish church announcements
Post event information and feeds
Upload daily word images
Program Management
Create and manage weekly church programs
Manage pastor availability schedule (Horaire)
Language Management
Upload content based on language:
English
French
Swahili
Kirundi
User Engagement Monitoring
View number of video views
View audio listening statistics
Monitor content likes and shares
Notification Management
Send notifications automatically when new content is published
The Admin Dashboard allows the church team to easily control and update all information displayed in the mobile application.

8. Development & Deployment Costs
Item
Cost
VPS Server (Hosting)
850,000 FBU
Domain Name
170,500 FBU
Payment Integration API
301,000 FBU
Mobile App Development
500,000 FBU
Google Play Store Publishing
150,000 FBU

Total Estimated Cost
1,971,500 FBU

9. Conclusion
The POWER OF THE WORLD mobile application will provide a modern digital platform for the church to share sermons, communicate with believers, broadcast services, and grow the ministry.
By combining video, live streaming, radio, daily teachings, and church information, the application ensures that members stay spiritually connected wherever they are.
This platform will help extend the reach and impact of the church worldwide.

# Media Church Backend API

A **Django REST Framework backend** that powers a **church media mobile application**.
The system manages **videos, live streams, audio sermons, radio broadcasts, feeds, daily words, and programs** while tracking **views, likes, shares, and listeners** from Android devices.

This backend is designed to work with a **Kotlin Android application** and provides REST APIs for all media features.

---

# Project Features

The system provides the following features:

### Video Module

* Store preaching videos
* Track video views per device
* Track likes
* Track shares
* Filter videos by language
* Filter videos by type

Video Types:

* testimony
* preach
* live

---

### Live Streaming

* Manage live church broadcasts
* Track viewers by device
* Show only active live streams
* Automatically delete viewer logs when live stops

---

### Audio Sermons

* Upload sermon audio
* Title automatically generated:

```
ISEGERO + current date
```

Example:

```
ISEGERO 2026-03-14
```

Users can:

* Listen
* Like
* Share

---

### Feeds (Announcements)

Church announcements such as:

* Prayer meetings
* Special events
* Church news

Feed Types:

```
igikorane
itaganzo
```

---

### Daily Word

Displays a **daily scripture image** for mobile users.

---

### Radio Broadcast

Church radio programs that become active automatically when current time is between:

```
start_hour → end_hour
```

Example:

```
Start: 08:00
End: 10:00
```

Radio becomes active automatically.

---

### Programs

Church schedule system including:

* Sunday service
* Weekly meetings
* Special events

Each program includes:

* title
* description
* day
* start hour
* end hour

---

### Horaire (Contacts / Schedule)

Stores contact information such as:

* prayer lines
* church contacts
* leaders

---

### Android Push Notifications

Automatic notifications are sent when:

* new video is added
* new audio is added
* live stream starts
* radio broadcast begins

Notifications are delivered through **Firebase Cloud Messaging (FCM)**.

---

# Supported Languages

The application supports four languages:

| Code | Language |
| ---- | -------- |
| FR   | French   |
| EN   | English  |
| SW   | Swahili  |
| RN   | Kirundi  |

---

# Device Tracking

The mobile app identifies users using:

```
device_id
```

Example in Android:

```kotlin
val deviceId = Settings.Secure.getString(
    context.contentResolver,
    Settings.Secure.ANDROID_ID
)
```

Device ID is used for:

* views
* likes
* shares
* listens

---

# Project Architecture

```
media_app
│
├── models.py
├── serializers.py
├── views.py
├── urls.py
├── admin.py
├── signals.py
├── services.py
├── notifications.py
```

---

# Models Overview

Main models in the system:

```
Video
ViewVideo
LikeVideo
ShareVideo

Live
ViewLive

Audio
ListenAudio
LikeAudio
ShareAudio

Feeds
Horaire
Radio
DailyWord
Program
```

---

# API Endpoints

Base URL:

```
/api/
```

---

## Videos

Get all videos

```
GET /api/videos/
```

Filters:

```
?language=EN
?type=preach
```

Record view

```
POST /api/viewvideo/
```

Like video

```
POST /api/likevideo/
```

Share video

```
POST /api/sharevideo/
```

---

## Live Streams

Get live streams

```
GET /api/live/
```

Register live viewer

```
POST /api/viewlive/
```

---

## Audio

Get audio sermons

```
GET /api/audio/
```

Register listen

```
POST /api/listenaudio/
```

Like audio

```
POST /api/likeaudio/
```

Share audio

```
POST /api/shareaudio/
```

---

## Feeds

```
GET /api/feeds/
```

Filters:

```
?language=RN
?type=igikorane
```

---

## Daily Word

```
GET /api/dailyword/
```

---

## Radio

```
GET /api/radio/
```

Returns radio status.

If:

```
is_active = true
```

the mobile app should start streaming.

---

## Programs

```
GET /api/programs/
```

---

## Horaire

```
GET /api/horaire/
```

---

# Installation

### Clone repository

```
git clone https://github.com/your-repo/church-media-backend.git
```

---

### Create virtual environment

```
python -m venv venv
```

Activate environment

Linux / Mac

```
source venv/bin/activate
```

Windows

```
venv\Scripts\activate
```

---

### Install dependencies

```
pip install -r requirements.txt
```

Main packages:

* Django
* Django REST Framework
* Pillow
* django-filter
* pyfcm
* django-cors-headers

---

### Run migrations

```
python manage.py makemigrations
python manage.py migrate
```

---

### Create admin user

```
python manage.py createsuperuser
```

---

### Run server

```
python manage.py runserver
```

Access admin panel:

```
http://127.0.0.1:8000/admin
```

---

# Media Files

Media files include:

* audio
* thumbnails
* feeds images
* daily word images

Configured in settings:

```
MEDIA_URL = '/media/'
MEDIA_ROOT = BASE_DIR / 'media'
```

---

# Push Notifications

Push notifications use **Firebase Cloud Messaging**.

Install library:

```
pip install pyfcm
```

Add Firebase server key in settings:

```
FCM_SERVER_KEY = "YOUR_FIREBASE_KEY"
```

Android devices should subscribe to topic:

```
all
```

---

# Radio Automatic Activation

Radio status updates automatically based on time.

Logic:

```
if start_hour <= current_time <= end_hour
    radio.is_active = True
else
    radio.is_active = False
```

This runs using:

* cron
* celery beat
* scheduled task

---

# Android Integration

Recommended Android libraries:

Networking:

```
Retrofit
OkHttp
```

Media:

```
ExoPlayer
```

Notifications:

```
Firebase Cloud Messaging
```

---

# Security Notes

Recommended improvements for production:

* add authentication
* prevent multiple likes per device
* rate limit API
* use Redis caching
* add analytics tracking

---

# Future Improvements

Possible enhancements:

* trending videos
* video analytics
* live chat
* podcast system
* podcast downloads
* playlist system
* admin analytics dashboard

---

# License

This project is intended for **church media streaming applications**.

---

# Author

Backend Developer:
Django REST Framework API for Android Media App

