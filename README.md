# <Mealplanner>

An Android app for planning meals and discovering new recipes.<br>
It helps users discover get an overview of the nutritional value of planed meal and allows for automatic creation of a shopping list. <br>
Own recipes can also be created and published (to the backend). <br>
The translation into both supported languages(EN/DE) and the calculation of the macronutrients  will happen automatically on publish.<br>

---
## Getting Started

The app can be installed directly from the [Google Play Store](<https://play.google.com/store/apps/details?id=com.felix.mealplanner20&pli=1>).

---

## Screenshots
| Discover Recipes | Nutrition Stats | Settings | Recipe View                                                         |
|------|---------|----------|---------------------------------------------------------------------|
| <img src="app/screenshots/screenshot_discover_view.png" width="250"/> | <img src="app/screenshots/screenshot_nutrition_view.png" width="250"/> | <img src="app/screenshots/screenshot_settings_view.png" width="250"/> | <img src="app/screenshots/screenshot_recipe_view.png" width="250"/> |

---

## Tech Stack
**Language:** Kotlin  
**UI:** Jetpack Compose
**Architecture:** MVVM  Architecture (View → ViewModel → UseCase → Repository → DAO)  
**Async / State:** Coroutines, Flow  
**DI:** Hilt
**Networking / Serialization:** <Retrofit, OkHttp, Moshi, Kotlinx Serialization>  
**Persistence:** Room
**Navigation:** Navigation Compose
**Image Loading:** Coil  
**Google Services:** Firebase Crashlytics

## Data Model
| <img src="app/screenshots/data_model.png" width="1250"/> | 

## Backend Access
This project uses a closed-source backend. Backend access is abstracted via interfaces


