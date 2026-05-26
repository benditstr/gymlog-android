<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Architecture-MVVM-6200EE?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Database-Room-00897B?style=for-the-badge&logo=sqlite&logoColor=white"/>
  <img src="https://img.shields.io/badge/UI-Material%20Design%203-757575?style=for-the-badge&logo=material-design&logoColor=white"/>
</p>

<h1 align="center">GymLog</h1>
<p align="center">A clean, offline-first Android fitness tracker designed for efficient gym session logging — built entirely from scratch as a university project.</p>

---

## Overview

GymLog is a native Android application that lets users log exercises, sets, and reps with a minimal, distraction-free interface. Designed with a dark purple-teal Material Design 3 theme, it prioritizes usability during an actual workout — no ads, no account required, everything stored locally on-device.

Built as a 4th-semester Media Engineering project at a German university, the app demonstrates practical application of Android development best practices including MVVM architecture, reactive UI with LiveData, and structured local persistence with Room.

---

## Features

- **Exercise Logging** — Log exercises by muscle group with set/rep tracking
- **Training History** — Browse past sessions organized by date
- **Statistics View** — Track progress over time per exercise
- **Offline-First** — All data stored locally via Room; no internet required
- **Dark Theme** — Custom purple-teal Material Design 3 color scheme
- **Clean Navigation** — Bottom navigation bar with 4 dedicated tabs

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Architecture | MVVM (Model-View-ViewModel) |
| UI Framework | Material Design 3 |
| Local Database | Room (SQLite abstraction) |
| Reactive Data | LiveData + ViewModel |
| Navigation | Fragment-based with BottomNavigationView |
| Build System | Gradle |
| Min SDK | Android 8.0 (API 26) |

---

## Architecture

The app follows the **MVVM pattern** recommended by Google's Android Architecture Guidelines:

```
UI Layer (Fragments / Activities)
        │
        ▼
ViewModel Layer (business logic, state management)
        │
        ▼
Repository Layer (single source of truth)
        │
        ▼
Room Database (DAOs, Entities)
```

- **Entities** define the data model (Exercise, Session, Set)
- **DAOs** expose reactive queries via LiveData
- **Repositories** abstract the data source from the ViewModel
- **ViewModels** survive configuration changes and expose UI state
- **Fragments** observe LiveData and update the UI reactively

---

## Project Structure

```
app/
├── data/
│   ├── database/       # Room database setup
│   ├── dao/            # Data Access Objects
│   ├── entity/         # Room entities
│   └── repository/     # Repository classes
├── ui/
│   ├── exercises/      # Exercise list & detail fragments
│   ├── history/        # Session history fragments
│   ├── statistics/     # Progress & stats fragments
│   └── settings/       # App settings
└── viewmodel/          # ViewModels per feature
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 26+
- Java 11

### Run Locally

```bash
git clone https://github.com/benditstr/gymlog-android.git
cd gymlog-android
```

Open in Android Studio → Sync Gradle → Run on emulator or physical device.

---

## What I Learned

- Structuring a real Android app end-to-end with clean separation of concerns
- Working with Room's reactive query system and LiveData observers
- Building a consistent design system with Material Design 3 theming
- Managing Fragment lifecycle and ViewModel scoping correctly
- Iterative UI development based on real usability feedback during workouts

---

## Status

Completed as a university assignment. Active development may continue as personal features are added.

---

<p align="center">Made with ☕ during Media Engineering studies · Nuremberg, Germany</p>
