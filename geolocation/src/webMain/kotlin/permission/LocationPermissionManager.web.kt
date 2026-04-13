@file:OptIn(ExperimentalWasmJsInterop::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package permission

import kotlinx.coroutines.flow.StateFlow
import kotlin.js.js
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.js.ExperimentalWasmJsInterop


@PublishedApi
internal actual class LocationPermissionManager actual constructor() {
    private val _state = MutableStateFlow(PermissionState.NOT_DETERMINED)
    actual val state: StateFlow<PermissionState> = _state.asStateFlow()

    init {
        checkPermission()
    }


    actual fun checkPermission() {
        jsCheckPermission { status ->
            _state.value = when (status) {
                "granted" -> PermissionState.GRANTED
                "denied" -> PermissionState.DENIED
                else -> PermissionState.NOT_DETERMINED
            }
        }
    }

    actual fun requestPermission() {
        jsRequestPermission(
            onSuccess = {
                _state.value = PermissionState.GRANTED
            },
            onDenied = {
                _state.value = PermissionState.DENIED
            }
        )
    }

}


private fun jsCheckPermission(onResult: (String) -> Unit): Unit = js("""{
    if (navigator.permissions) {
        navigator.permissions.query({name: 'geolocation'}).then(function(result) {
            onResult(result.state);
        });
    } else {
        onResult("prompt");
    }
}""")



private fun jsRequestPermission(onSuccess: () -> Unit, onDenied: () -> Unit): Unit = js("""{
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(pos) { onSuccess(); },
            function(err) { onDenied(); }
        );
    } else {
        onDenied();
    }
}""")