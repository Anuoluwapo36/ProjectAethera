# Project Aethera

A native Android client for Aethera, a mobile social network for sharing photos. This repository holds the Android application, written in Java against the Android View system and structured around MVVM. It talks to a companion Node.js service over a REST API.

![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84)
![Target SDK](https://img.shields.io/badge/target%20SDK-36-3DDC84)
![Java](https://img.shields.io/badge/Java-11-ED8B00)
![Gradle](https://img.shields.io/badge/Gradle-8.13-02303A)
![License](https://img.shields.io/badge/license-MIT-blue)

## Overview

Aethera is a photo-sharing network built around a feed. People publish images, follow accounts they care about, react to posts, and hold conversations in threaded comment threads. Alongside the feed, the app carries a status surface for short-lived posts that disappear after a day.

The Android client covers the whole member journey: signing in and out, publishing, reacting, browsing profiles, searching for people, and keeping up with activity through a notification centre. All screen content is served by the Aethera API, which runs as a separate service and keeps its data in MySQL.

## Features

**Accounts and access**

- Sign up with username, email, and password, with a terms check before the account is created
- Sign in and stay signed in across launches
- Reset a forgotten password through an emailed verification code
- Temporary account lock after a run of failed sign-in attempts
- Email availability checked before registration completes

**Feed and publishing**

- Paginated home feed assembled from the accounts a member follows
- Pull to refresh, with a shimmer placeholder while images load
- Like and unlike with an immediate visual response that keeps the feed in place
- Publish a post from the camera or the gallery, with one image or several
- Review a post before it goes live, then delete it later
- Follow or unfollow straight from the feed

**Conversation**

- Comments open in a bottom sheet that sits over the feed
- Nested replies that stay attached to the comment they answer
- Reply targeting that reaches the right person
- Comment counts shown on every post

**Profiles and discovery**

- Own profile and other people's profiles behind the same screen
- Editable username, bio, and profile picture
- Grid of published posts, with a detail view for a single post
- Follower and following counts
- Search for people by name or username

**Statuses**

- Status strip across the top of the feed
- Publish a status from the gallery and preview it first
- Watch statuses from other people, like them, and remove your own
- Statuses stay limited to mutual connections and expire after 24 hours

**Notifications**

- Activity centre covering likes, comments, replies, follows, and status likes
- Unread badge on the bell and in the navigation bar
- System alerts raised while the app is open, on Android 13 and above after the notification permission is granted
- Mark one notification read, or clear the lot

**Preferences**

- Dark mode that applies immediately across the app
- Separate switches for likes, comments, follows, and push notifications
- Configurable sign-in attempt limit
- Profile editing, account sign-out, and an about page

## Architecture

Screens are Fragments paired with XML layouts. Each screen binds to a ViewModel that publishes state as LiveData, and every ViewModel hands work to a repository. Repositories own the Retrofit interface and turn responses into the models the interface layer consumes. One-off signals such as navigation and snackbars travel through a small LiveEvent wrapper, so a configuration change cannot replay them.

Networking goes through a single Retrofit client on OkHttp, with Gson handling serialization and an interceptor attaching the API credential to every request. Multipart requests carry post and status media to the server. Images load through Glide, with a Shimmer placeholder covering the network round trip while a post renders.

MainActivity hosts four bottom-navigation tabs  feed, search, create, and profile  plus a second fragment container that stacks overlay flows such as comments, settings, the gallery, and post creation over the feed. Keeping them in a separate container leaves tab state untouched underneath.

Session state and user preferences live in SharedPreferences. There is no local database: the feed, profiles, and notifications are read from the API on each load.

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Java 11, Gradle Kotlin DSL |
| SDK | Minimum API 24, target and compile API 36 |
| Architecture | MVVM with ViewModel and LiveData |
| Networking | Retrofit 2.11, OkHttp 4.10, Gson |
| Images | Glide 4.15, Facebook Shimmer |
| Camera | AndroidX CameraX 1.3 |
| Interface | Material Components 1.12, AndroidX Views, ViewPager2 |
| Motion | Lottie 6.6 |
| Build | Gradle 8.13, Android Gradle Plugin 8.11 |

## Building and Running

The project needs Android Studio with JDK 17 or newer and Android SDK 36, plus an Aethera API service listening on port 8000.

Two settings are read from `local.properties`, which is excluded from version control:

| Property | Purpose |
|---|---|
| `AETHERA_API_KEY` | Credential sent in the `x-api-key` header on every request |
| `AETHERA_DEV_HOST` | Backend base URL used when running on a physical device |

On the emulator the app reaches the host machine at `10.0.2.2:8000` on its own. A physical device needs the LAN address of the machine running the service, set through `AETHERA_DEV_HOST`. Either property can also be supplied as a Gradle property or an environment variable.

Open the project in Android Studio, let Gradle sync, and run the `app` configuration on a connected device or emulator. From a shell, `./gradlew assembleDebug` produces the debug APK. Register an account on first launch to populate the feed.

## Permissions

| Permission | Used for |
|---|---|
| `INTERNET` | API calls and media loading |
| `CAMERA` | Capturing photos for posts and statuses |
| `READ_MEDIA_IMAGES` | Picking images from the gallery, Android 13 and above |
| `READ_EXTERNAL_STORAGE` | Picking images from the gallery, below Android 13 |
| `POST_NOTIFICATIONS` | System alerts, Android 13 and above |

## Contributing

Work happens on feature branches. Keep a change focused on one behaviour, match the surrounding Java style, and confirm the project still assembles before opening a pull request. Describe the behaviour change and the screens it touches in the pull request body.

## License

This project is released under the MIT License.

## Authors

Project Aethera is designed and maintained by [Anuoluwapo Majekobaje Emmanuel](https://github.com/Anuoluwapo36).
