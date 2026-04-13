@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package permission


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import provider.GeoLocationContextProvider
import provider.PermissionActivity

@PublishedApi
internal actual class LocationPermissionManager actual constructor() {

    private val context = GeoLocationContextProvider.applicationContext
        ?: throw IllegalStateException("GeoLocationContextProvider is not initialized.")

    private val _state = MutableStateFlow(PermissionState.NOT_DETERMINED)
    actual val state: StateFlow<PermissionState> = _state.asStateFlow()

    init {
        checkPermission()
    }

    actual fun checkPermission() {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        _state.value = if (hasFine || hasCoarse) PermissionState.GRANTED else PermissionState.DENIED
    }

    actual fun requestPermission() {
        if (_state.value == PermissionState.GRANTED) return


        activeManager = this

        val intent = Intent(context, PermissionActivity::class.java).apply {
            putExtra("permissions", arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var activeManager: LocationPermissionManager? = null

        internal fun handlePermissionResult(isGranted: Boolean) {
            activeManager?._state?.value = if (isGranted) PermissionState.GRANTED else PermissionState.DENIED
            activeManager = null // Clear to prevent memory leaks
        }
    }
}


