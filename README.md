# knowva-mobile (Android)

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Java-blue?style=flat-square)](#)
[![Material](https://img.shields.io/badge/UI-Material%20Components-673ab7?style=flat-square)](#)
[![Retrofit](https://img.shields.io/badge/Networking-Retrofit2-orange?style=flat-square)](#)
[![minSdk](https://img.shields.io/badge/minSdk-26-informational?style=flat-square)](#)
[![targetSdk](https://img.shields.io/badge/targetSdk-36-informational?style=flat-square)](#)

A flashcard & quiz learning app for PRM392.  
The app supports authentication, a rich Home feed, Bottom Navigation (Home / Flashcard / Quiz), viewing **My flashcard sets**, and a **Create Flashcard Set** flow with server sync.

> This project is student-built for learning. Issues and pull requests are welcome.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Contract](#api-contract)
- [Create Flashcard Set Flow](#create-flashcard-set-flow)
- [Troubleshooting](#troubleshooting)
- [Roadmap](#roadmap)
- [License](#license)

---

## Overview

- **Auth**: Sign in / Sign up via REST API, token saved locally.
- **Home**: Drawer + Toolbar + multi-section RecyclerView (banner, “continue learning”, featured authors, suggestions).
- **Bottom Tab**: `Home`, `Flashcard`, `Quiz`.
- **Your library**: Fetch and display **my flashcard sets** (grouped by month).
- **Create Flashcard Set**: Editor for term/definition cards, then metadata screen (visibility, category, language…), finally POST to server.

---

## Architecture

- **Pattern**: Lightweight MVVM / Repository.
- **Networking**: Retrofit2 + OkHttp; `AuthInterceptor` injects **Bearer** token.
- **UI**: Material Components, RecyclerView, CardView.
- **Storage**: `SharedPreferences` for access token.

---

## Features

1. **Authentication**
   - `POST /login`, `POST /register`
   - Token persisted in `SharedPreferences("MyAppPrefs", "access_token")`.

2. **Home**
   - `HomeActivity` shows banner, sections, horizontal carousels and recommendations.

3. **Bottom Navigation**
   - `BottomNavigationView` with **Home / Flashcard / Quiz** (`res/menu/menu_bottom_nav_main.xml`).
   - Present on Home/Library/Create screens for consistent UX.

4. **Flashcard Library**
   - `GET /flashcard-sets/my-flashcard-sets`
   - Group by month (`createdAt`) → section headers + items.

5. **Create Flashcard Set**
   - Screen 1: Title/Description + dynamic list of *Term/Definition* (underline inputs).
   - Screen 2: Options (visibility, category, language, card type, source type).
   - POST to server; success → toast + back to Home.

---

## Project Structure

```
app/src/main/java/com/prm392/knowva_mobile
├── factory/
│   ├── APIClient.java           # Retrofit + OkHttp client
│   └── AuthInterceptor.java     # Adds Authorization header
├── model/
│   ├── FlashcardSet.java
│   ├── User.java
│   ├── request/
│   │   ├── SignIn.java
│   │   └── SignUp.java
│   └── response/
│       ├── AuthResponse.java
│       └── MyFlashcardSetResponse.java
├── repository/
│   ├── AuthRepository.java
│   ├── HomeRepository.java      # currently mock data
│   └── FlashcardRepository.java
├── service/
│   ├── AuthService.java
│   ├── HomeService.java         # placeholder
│   └── FlashcardService.java    # GET my-sets, POST save
└── view/
    ├── HomeActivity.java
    └── flashcard/
        ├── FlashcardBottomSheet.java
        ├── MyFlashcardsActivity.java
        ├── CreateSetActivity.java
        ├── CreateSetMetaActivity.java
        ├── adapter/CardEditorAdapter.java
        └── model/
            ├── CardDraft.java
            └── CreateSetRequest.java
```

Key layouts & menus:
```
res/layout/
  activity_home.xml
  bottom_sheet_flashcard.xml
  activity_my_flashcards.xml
  item_month_header.xml
  item_my_flashcard_set.xml
  activity_create_set.xml
  item_card_editor.xml
  activity_create_set_meta.xml

res/menu/
  home_toolbar_menu.xml
  menu_bottom_nav_main.xml
  menu_create_set_top.xml
```

---

## Getting Started

### Clone & open with Android Studio
```bash
git clone https://github.com/TranPhuKhag/knowva-mobile-app.git
```
1. Open the project in **Android Studio Koala+**.
2. Let Gradle sync and build.
3. Run on a device/emulator (**minSdk 26**, **targetSdk 36**).

---

## Configuration

### Base URL
`factory/APIClient.java`
```java
private static String baseURL = "https://api.knowva.me/api/";
// for dev env, you may switch to: "https://api.dev.knowva.me/api/"
```

### Token storage
- After successful login/register, save token to:
  - `SharedPreferences` name: **"MyAppPrefs"**
  - key: **"access_token"**
- `AuthInterceptor` skips `login` & `register`, and adds the header for others:
  ```
  Authorization: Bearer <token>
  ```

### Timeouts
- OkHttp call/read/write timeouts: **90s** (configured in `APIClient.java`).

---

## API Contract

**Base**: `https://api.knowva.me/api/`

### Auth
- `POST /login` → `AuthResponse { token }`
- `POST /register` → `AuthResponse { token }`

### Flashcards
- **List my sets**
  - `GET /flashcard-sets/my-flashcard-sets`
  - Headers: `Authorization: Bearer <token>`
  - Response: `List<MyFlashcardSetResponse>`

- **Create (save) a set**
  - `POST /flashcard-sets/save`
  - Headers: `Authorization: Bearer <token)`
  - Body:
    ```json
    {
      "title": "string",
      "description": "string",
      "sourceType": "PDF",
      "language": "VIETNAMESE",
      "cardType": "STANDARD",
      "visibility": "PUBLIC",
      "category": "HISTORY",
      "flashcards": [
        { "front": "string", "back": "string", "order": 1 },
        { "front": "string", "back": "string", "order": 2 }
      ]
    }
    ```
  - Notes:
    - Must include **at least 2** valid cards (both `front` & `back` non-empty).
    - Enum values are **UPPERCASE** as shown.

Example Retrofit interface:
```java
public interface FlashcardService {
    @GET("flashcard-sets/my-flashcard-sets")
    Call<List<MyFlashcardSetResponse>> getMySets();

    @POST("flashcard-sets/save")
    Call<Void> createSet(@Body CreateSetRequest body);
}
```

---

## Create Flashcard Set Flow

1. **Open:** Home → Bottom Tab **Flashcard** → Bottom sheet → **Flashcard set**.
2. **CreateSetActivity**
   - Inputs: Title, Description, and a list of **Term / Definition** rows (underline style; no borders).
   - Default: 2 empty rows; add more via **FAB (+)**.
   - **Done (✔)**:
     - If no input → close.
     - If < 2 valid cards → show dialog (“add at least two terms”).
     - Otherwise → go to **CreateSetMetaActivity**.
   - **Back (X)**:
     - If edited → confirm dialog (“Discard draft / Keep editing”).

3. **CreateSetMetaActivity**
   - Fields: Visibility (PUBLIC/PRIVATE), Category, Language, Card Type, Source Type.
   - **Done (✔)**: POST `/flashcard-sets/save`. On success → toast **Saved!** → navigate to **Home** (clear back stack).
   - **Back**: return to editor preserving inputs.

---

## Troubleshooting

- **401 / 403 on any API**
  - Token missing/expired. Ensure it’s saved to `SharedPreferences("MyAppPrefs", "access_token")`.
- **400 when creating a set**
  - Using wrong endpoint (must be `/flashcard-sets/save`).
  - Less than 2 valid cards.
  - Enum fields not uppercase or missing required fields.
  - Inspect `response.errorBody()` with an HTTP logger to see backend message.
- **“Your library” empty**
  - Verify `Authorization` header is present and base URL points to the environment that holds your data.
- **Toolbar title overlaps**
  - Ensure the content view scrolls under AppBar with `app:layout_behavior="@string/appbar_scrolling_view_behavior"` and that only one Toolbar title is shown.

---

## Roadmap

- Study/learn modes for a set  
- Search & filters  
- Offline caching  
- Quiz module  
- UI polish & animations  

---

## License

For educational purposes (student project). License TBA.
