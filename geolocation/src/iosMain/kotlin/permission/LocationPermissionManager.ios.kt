@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package permission


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.CoreLocation.*
import platform.darwin.NSObject

@PublishedApi
internal actual class LocationPermissionManager actual constructor() {
    private val locationManager = CLLocationManager()

    private val _state = MutableStateFlow(PermissionState.NOT_DETERMINED)
    actual val state: StateFlow<PermissionState> = _state.asStateFlow()

    // We must hold a reference to the delegate so it isn't garbage collected
    private val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            updateStateFromNative(manager.authorizationStatus)
        }
    }

    init {
        locationManager.delegate = delegate
        checkPermission()
    }

    actual fun checkPermission() {
        updateStateFromNative(CLLocationManager.authorizationStatus())
    }

    actual fun requestPermission() {
        locationManager.requestWhenInUseAuthorization()
    }

    private fun updateStateFromNative(status: CLAuthorizationStatus) {
        _state.value = when (status) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> PermissionState.GRANTED

            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> PermissionState.DENIED

            else -> PermissionState.NOT_DETERMINED
        }
    }
}