@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package permission

import kotlinx.coroutines.flow.StateFlow


@PublishedApi
internal expect class LocationPermissionManager() {

    val state: StateFlow<PermissionState>

    fun checkPermission()

    fun requestPermission()
}

