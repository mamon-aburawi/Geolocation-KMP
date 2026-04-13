@file:OptIn(ExperimentalWasmJsInterop::class)

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

internal external interface GeolocationPosition : JsAny {
    val coords: GeolocationCoordinates
    val timestamp: Double
}
