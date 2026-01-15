# <Mealplanner>

An Android app for planning meals and discovering new recipes.<br>
It helps users discover get an overview of the nutritional value of planed meal and allows for automatic creation of a shopping list. <br>
Own recipes can also be created and published (to the backend). <br>
The translation into both supported languages(EN/DE) and the calculation of the macronutrients  will happen automatically on publish.<br>

---
## Getting Started

The app can be installed directly from the [Google Play Store](<link>).

---

## Screenshots
| Discover Recipes | Nutrition Stats | Settings |
|------|---------|----------|
| <img src="screenshots/screenshot_discover_view.png" width="250"/> | <img src="screenshots/screenshot_nutrition_view.png" width="250"/> | <img src="screenshots/screenshot_settings_view.png" width="250"/> |

---

## Tech Stack
**Language:** Kotlin  
**UI:** <Jetpack Compose>
**Architecture:** MVVM  Architecture (View → ViewModel → UseCase → Repository → DAO)  
**Async / State:** Coroutines, Flow  
**DI:** <Hilt>  
**Networking / Serialization:** <Retrofit, OkHttp, Moshi, Kotlinx Serialization>  
**Persistence:** <Room>
**Navigation:** <Navigation Compose>
**Image Loading:** Coil  
**Google Services:** Firebase (<Crashlytics>)  
