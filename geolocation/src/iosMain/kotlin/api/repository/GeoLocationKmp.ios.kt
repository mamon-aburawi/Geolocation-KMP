@file:OptIn(ExperimentalForeignApi::class)

package api.repository

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import model.KmpLocation
import model.LocationAccuracy
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.CoreLocation.kCLLocationAccuracyHundredMeters
import platform.CoreLocation.kCLLocationAccuracyKilometer
import platform.Foundation.timeIntervalSince1970
import platform.darwin.NSObject


import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


internal class AppleGeolocationKmp : GeolocationKmp {
    private val locationManager = CLLocationManager()

    private fun LocationAccuracy.toAppleAccuracy() = when(this) {
        LocationAccuracy.HIGH -> kCLLocationAccuracyBest
        LocationAccuracy.BALANCED -> kCLLocationAccuracyHundredMeters
        LocationAccuracy.LOW_POWER -> kCLLocationAccuracyKilometer
    }

    override suspend fun getCurrentLocation(accuracy: LocationAccuracy): KmpLocation? {

        // 1. Hardware Check (Global iOS Location Services toggle)
        if (!CLLocationManager.locationServicesEnabled()) {
            throw GeoLocationException.ServiceDisabled("iOS Location Services are disabled.")
        }

        // 2. Fetch Location Async (Permissions handled safely by LocationPermissionManager)
        return suspendCancellableCoroutine { continuation ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                    manager.stopUpdatingLocation()
                    manager.delegate = null

                    val location = didUpdateLocations.lastOrNull() as? CLLocation
                    if (location != null && continuation.isActive) {
                        continuation.resume(location.toKmpLocation())
                    } else if (continuation.isActive) {
                        continuation.resumeWithException(GeoLocationException.HardwareTimeout("No location data received."))
                    }
                }

                override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                    manager.stopUpdatingLocation()
                    manager.delegate = null
                    if (continuation.isActive) {
                        continuation.resumeWithException(GeoLocationException.HardwareTimeout(didFailWithError.localizedDescription))
                    }
                }
            }

            locationManager.delegate = delegate
            locationManager.desiredAccuracy = accuracy.toAppleAccuracy()

            // Go straight to updating location since we know we have permission
            locationManager.startUpdatingLocation()

            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
                locationManager.delegate = null
            }
        }
    }

    private fun CLLocation.toKmpLocation() = KmpLocation(
        latitude = coordinate.useContents { latitude },
        longitude = coordinate.useContents { longitude },
        altitude = altitude,
        speed = speed.toFloat().takeIf { it >= 0 },
        bearing = course.toFloat().takeIf { it >= 0 },
        accuracy = horizontalAccuracy.toFloat(),
        timestamp = (timestamp.timeIntervalSince1970 * 1000).toLong()
    )
}


actual fun getGeolocationProvider(): GeolocationKmp = AppleGeolocationKmp()




