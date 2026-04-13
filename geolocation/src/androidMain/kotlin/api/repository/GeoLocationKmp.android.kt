package api.repository


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import model.KmpLocation
import model.LocationAccuracy
import android.location.LocationManager
import android.provider.Settings
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import provider.GeoLocationContextProvider
import kotlin.coroutines.resumeWithException


internal class AndroidGeolocationKmp : GeolocationKmp {

    private val context: Context
        get() = GeoLocationContextProvider.applicationContext
            ?: throw IllegalStateException("GeoLocationContextProvider is not initialized. Please provide the application context.")

    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private fun LocationAccuracy.toAndroidPriority() = when (this) {
        LocationAccuracy.HIGH -> Priority.PRIORITY_HIGH_ACCURACY
        LocationAccuracy.BALANCED -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
        LocationAccuracy.LOW_POWER -> Priority.PRIORITY_LOW_POWER
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    private fun promptUserToEnableLocation() {
        // Automatically opens the Android Settings menu for the user to turn on GPS
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(accuracy: LocationAccuracy): KmpLocation? {

        // 1. Hardware Check (GPS turned on)
        if (!isLocationEnabled()) {
            promptUserToEnableLocation()
            throw GeoLocationException.ServiceDisabled("GPS is turned off.")
        }

        // 2. Fetch Location Async (Permissions are now handled safely by LocationPermissionManager)
        return suspendCancellableCoroutine { continuation ->
            try {
                val task = locationClient.getCurrentLocation(accuracy.toAndroidPriority(), null)

                task.addOnSuccessListener { location: Location? ->
                    if (location != null && continuation.isActive) {
                        continuation.resume(location.toKmpLocation())
                    } else if (continuation.isActive) {
                        // Sometimes FusedLocationProvider returns null if the device has never gotten a fix
                        continuation.resumeWithException(GeoLocationException.HardwareTimeout("Google Play Services returned a null location."))
                    }
                }

                task.addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(GeoLocationException.HardwareTimeout(exception.message ?: "Failed to get location."))
                    }
                }

                task.addOnCanceledListener {
                    continuation.cancel()
                }
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resumeWithException(GeoLocationException.HardwareTimeout(e.message ?: "Unknown hardware error."))
                }
            }
        }
    }

    private fun Location.toKmpLocation() = KmpLocation(
        latitude = latitude,
        longitude = longitude,
        altitude = if (hasAltitude()) altitude else null,
        speed = if (hasSpeed()) speed else null,
        bearing = if (hasBearing()) bearing else null,
        accuracy = if (hasAccuracy()) accuracy else null,
        timestamp = time
    )
}



actual fun getGeolocationProvider(): GeolocationKmp = AndroidGeolocationKmp()


