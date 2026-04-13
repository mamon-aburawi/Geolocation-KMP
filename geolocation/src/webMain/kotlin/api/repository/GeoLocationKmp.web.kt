@file:OptIn(ExperimentalWasmJsInterop::class)
@file:Suppress("CAST_NEVER_SUCCEEDS")

package api.repository


import GeolocationPosition
import GeolocationPositionError
import PositionOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import model.KmpLocation
import model.LocationAccuracy
import navigator
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js
import kotlin.js.JsAny
import kotlin.coroutines.resumeWithException


internal fun createOptions(highAccuracy: Boolean): PositionOptions =
    js("({ enableHighAccuracy: highAccuracy })")

internal external interface Geolocation : JsAny {
    fun getCurrentPosition(success: (GeolocationPosition) -> Unit, error: (GeolocationPositionError) -> Unit, options: PositionOptions)
}


internal class WebGeolocationKmp : GeolocationKmp {

    override suspend fun getCurrentLocation(accuracy: LocationAccuracy): KmpLocation? {
        val geo = navigator.geolocation ?: throw GeoLocationException.HardwareTimeout("api.repository.Geolocation API is not supported in this browser.")

        return suspendCancellableCoroutine { cont ->
            geo.getCurrentPosition(
                success = { pos ->
                    if (cont.isActive) {
                        cont.resume(
                            KmpLocation(
                                latitude = pos.coords.latitude,
                                longitude = pos.coords.longitude,
                                accuracy = pos.coords.accuracy.toFloat(),
                                altitude = pos.coords.altitude,
                                timestamp = pos.timestamp.toLong()
                            )
                        )
                    }
                },
                error = { err ->
                    if (cont.isActive) {
                        val exception = when (err.code.toInt()) {
                            1 -> GeoLocationException.PermissionMissing("Location permission denied by browser.")
                            2 -> GeoLocationException.ServiceDisabled("Location Services disabled in OS.")
                            3 -> GeoLocationException.HardwareTimeout("Browser location request timed out.")
                            else -> GeoLocationException.HardwareTimeout(err.message)
                        }
                        cont.resumeWithException(exception)
                    }
                },
                options = createOptions(accuracy == LocationAccuracy.HIGH)
            )
        }
    }
}

actual fun getGeolocationProvider(): GeolocationKmp = WebGeolocationKmp()


