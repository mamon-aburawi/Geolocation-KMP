@file:OptIn(ExperimentalWasmJsInterop::class)

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

internal external interface GeolocationPositionError : JsAny {
    val code: Short
    val message: String
}
