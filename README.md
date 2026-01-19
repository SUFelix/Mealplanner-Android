# Mealplanner

An Android app for planning meals and discovering new recipes.<br>
It helps users discover get an overview of the nutritional value of planed meal and allows for automatic creation of a shopping list. <br>
Own recipes can also be created and published (to the backend). <br>
The translation into both supported languages(EN/DE) and the calculation of the macronutrients  will happen automatically on publish.<br>

## Getting Started

The app can be installed directly from the [Google Play Store](<https://play.google.com/store/apps/details?id=com.felix.mealplanner20&pli=1>).


## Screenshots
| Discover Recipes | Nutrition Stats | Settings | Recipe View                                                         |
|------|---------|----------|---------------------------------------------------------------------|
| <img src="app/screenshots/screenshot_discover_view.png" width="250"/> | <img src="app/screenshots/screenshot_nutrition_view.png" width="250"/> | <img src="app/screenshots/screenshot_settings_view.png" width="250"/> | <img src="app/screenshots/screenshot_recipe_view.png" width="250"/> |


## Tech Stack
**Language:** Kotlin  <br>
**UI:** Jetpack Compose<br>
**Architecture:** MVVM  Architecture (View → ViewModel → UseCase → Repository → DAO)  <br>
**Async / State:** Coroutines, Flow  <br>
**DI:** Hilt <br>
**Networking / Serialization:** <Retrofit, OkHttp, Moshi, Kotlinx Serialization>  <br>
**Persistence:** Room <br>
**Navigation:** Navigation Compose <br>
**Image Loading:** Coil  <br>
**Google Services:** Firebase Crashlytics

## Data Model
| <img src="app/screenshots/data_model.png" width="1250"/> | 

## Backend Access
This project uses a closed-source backend. Backend access is abstracted via interfaces

## Long-Term Direction

### Custom Nutrition Metrics
The system supports a variety of **custom nutrition metrics** to evaluate nutritional quality. Instead of enforcing a single nutrition doctrine, the app allows different dietary theories and personal interpretations of "healthy".

Nutrition quality is treated as a **measurable but configurable concept**, not a fixed truth.

### Multiple Optimization Strategies
Meal planning is modeled as an optimization problem with interchangeable strategies, such as:
- local improvement (e.g. maximizing gain by replacing a single recipe)
- global optimization over an entire meal plan
- hybrid approaches combining personal preferences with nutritional quality

The optimization shall support all custom metrics, as mentioned above

### Multilingual by Design
The goal is to support all major European languages (≥ 40 million native speakers each).

### Fair, Non-Intrusive Monetization
The free version of the app is:
- fully usable
- ad-free
- sufficient to gain substantial value

A potential premium subscription is intended as a *nice-to-have* for advanced features, not a requirement for meaningful use.

### Offline-First Architecture
The app follows an **offline-first approach**:
- core functionality works without internet access
- data is stored locally and synchronized when connectivity is available
- temporary offline usage shall be as functional as possible 


