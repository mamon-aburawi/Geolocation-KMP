@file:OptIn(ExperimentalWasmJsInterop::class)

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny


internal external interface GeolocationCoordinates : JsAny {
    val latitude: Double
    val longitude: Double
    val altitude: Double?
    val accuracy: Double
}


