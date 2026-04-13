@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@PublishedApi
internal actual class LocationPermissionManager actual constructor() {
    // Desktop apps don't require mobile-style permission popups
    private val _state = MutableStateFlow(PermissionState.GRANTED)
//    actual val state: StateFlow<PermissionState> = _state.asStateFlow()

    actual fun checkPermission() {
        _state.value = PermissionState.GRANTED
    }

    actual fun requestPermission() {
        // No-op for Desktop. Automatically granted.
        _state.value = PermissionState.GRANTED
    }

    actual val state: StateFlow<PermissionState>
        get() = _state.asStateFlow()
}