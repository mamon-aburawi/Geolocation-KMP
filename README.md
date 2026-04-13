
# 🌍 GeoLocation-Kmp

A lightweight, modern, and coroutine-based Kotlin Multiplatform (KMP) library designed to effortlessly fetch precise GPS coordinates and seamlessly reverse-geocode them into localized, human-readable street addresses. 

GeoLocation-Kmp is built with resilience in mind. It abstracts away the complex boilerplate of platform-specific location services and gracefully handles, rate-limiting, and network errors—ensuring your app remains stable and responsive.

## ✨ Features

| Feature | Description |
| :--- | :--- |
| **🌐 True Multiplatform** | Full support for **Android, iOS, Desktop, and Web**. Write your location logic once and run it anywhere. |
| **🌍 Dynamic Localization** | Pass ISO language codes (like `"en"`, `"ar"`) to dynamically localize the street address results. |
| **🚦 Smart Throttling** | Built-in debouncer prevents API spam, protecting your application from bans and handling HTTP 429 (Rate Limit) errors automatically. |

---

## 🚀 Installation

Add the dependency to your `commonMain` source set in your `build.gradle.kts` file:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.mamon-aburawi:geolocation-kmp:{last_version}") 
        }
    }
}
```

---

## 🔐 Permissions Setup

Before requesting the user's location, you must configure the native permissions for both Android and iOS. 

### 🤖 Android
Add the following permissions to your `android/src/main/AndroidManifest.xml` file. 
*Note: Because this library uses OpenStreetMap for reverse geocoding, the `INTERNET` permission is also required.*

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

<uses-permission android:name="android.permission.INTERNET"/>
```

### 🍎 iOS
Apple strictly requires apps to explain *why* they need the user's location. You **must** add the following key to your iOS app's `iosApp/iosApp/Info.plist` file. If this is missing, iOS will instantly crash your app when a location request is made.

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>We need your location to show your current address.</string>
```

---

## 💻 Usage

The library provides a simple, unified `GeoLocation` interface. **Note:** OpenStreetMap policy strictly requires you to provide an email or agent name to identify your app during geocoding requests.

### 1. In Jetpack Compose (Android / Desktop / Web)
For Compose, you can create a `rememberGeoLocation` state helper so your class survives recompositions.

```kotlin
import io.mamon.geolocation.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch


@Composable
fun LocationScreen() {
    val scope = rememberCoroutineScope()
    
    val geoLocation = rememberGeoLocation(
        agentName = "your-contact-email@example.com", 
        languageCode = "en"
    )

    Button(onClick = {
        scope.launch {
            try {
                val location = geoLocation.findLocation()
                println("📍 Coordinates: ${location?.latitude}, ${location?.longitude}")
                println("🏠 Address: ${location?.fullAddress}")
                
            } catch (e: GeoLocationException) {
                // Handle domain-specific errors (e.g., show permission dialogs)
                println("Error: ${e.message}")
            }
        }
    }) {
        Text("Get My Location")
    }
}
```

### 2. Outside Compose (ViewModels / iOS / Native)
If you are working inside an Android `ViewModel`, a native iOS controller, or standard Kotlin code, you can initialize and use the library directly.

```kotlin
import io.mamon.geolocation.*
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class LocationViewModel : ViewModel() {

    // 1. Initialize directly
    private val geoLocation = GeoLocation(
        email = "your-contact-email@example.com",
        languageCode = "en" // Try "ar" for Arabic or "es" for Spanish!
    )

    fun fetchUserLocation() {
        // 2. Launch in your CoroutineScope
        viewModelScope.launch {
            try {
                // 3. Fetch location
                val location = geoLocation.findLocation()
                
                if (location != null) {
                    println("📍 Coordinates: ${location.latitude}, ${location.longitude}")
                    println("🏠 Address: ${location.fullAddress}")
                }
                
            } catch (e: GeoLocationException) {
                when (e) {
                    is GeoLocationException.PermissionMissing -> println("Missing Permissions! Please grant access.")
                    is GeoLocationException.ServiceDisabled -> println("GPS is off! Please enable location services.")
                    else -> println("Error: ${e.message}")
                }
            }
        }
    }
}
```

---

## ⚠️ Important API Notes & OpenStreetMap Policy

This library uses the free, public OpenStreetMap (Nominatim) server for reverse geocoding. By using this library, you agree to their [Usage Policy](https://operations.osmfoundation.org/policies/nominatim/).

1. **Email Required:** You must pass a valid `email` (or app identifier) to the `GeoLocation` class. This is used in the `User-Agent` header. Using a random or blank string will result in a permanent IP ban from OpenStreetMap.
2. **Rate Limits:** The API is strictly limited to **1 request per second**. This library implements an internal 2-second throttle to protect your app from throwing HTTP 429 Rate Limit errors if the user spams the location button.
