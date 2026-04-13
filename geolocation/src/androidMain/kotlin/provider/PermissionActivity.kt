package provider

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import permission.LocationPermissionManager

class PermissionActivity : ComponentActivity() {

    // 1. Modern Activity Result API replaces the deprecated requestPermissions()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 2. Check if at least one of the requested permissions (e.g., Coarse or Fine) was granted
        val isGranted = permissions.values.any { it }

        // Pass the result directly to your manager
        LocationPermissionManager.Companion.handlePermissionResult(isGranted)

        finishSilently()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableAnimations()

        val permissions = intent.getStringArrayExtra("permissions")
        if (permissions == null || permissions.isEmpty()) {
            finishSilently()
            return
        }

        // Trigger the OS popup via the launcher
        requestPermissionLauncher.launch(permissions)
    }

    private fun finishSilently() {
        finish()
        disableAnimations()
    }

    private fun disableAnimations() {
        // 3. Handle Android 14+ (API 34) transition deprecations safely
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}