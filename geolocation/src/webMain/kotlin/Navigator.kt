@file:OptIn(ExperimentalWasmJsInterop::class)

import api.repository.Geolocation
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

internal external interface Navigator : JsAny {
    val geolocation: Geolocation?
}

internal external val navigator: Navigator