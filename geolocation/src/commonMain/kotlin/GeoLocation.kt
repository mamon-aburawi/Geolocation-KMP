
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import api.repository.GeolocationKmp
import api.repository.OpenStreetGeocoder
import api.repository.getGeolocationProvider
import model.GeoMarker
import model.LocationAccuracy
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource


/**
 * @param email A valid contact email address. This is strictly required by the OpenStreetMap/Nominatim
 * usage policy to identify your application in the HTTP User-Agent header.

 * @param languageCode The ISO 639-1 language code (e.g., "en", "ar", "fr") used to request localized
 * street addresses. Defaults to "en" (English).
 */


class GeoLocation(
    val email: String,
    val languageCode: String = "en"
) {
    private val provider: GeolocationKmp = getGeolocationProvider()
    private val geocoder: OpenStreetGeocoder =
        OpenStreetGeocoder(email = email, languageCode = languageCode)


    private var lastRequestMark: TimeSource.Monotonic.ValueTimeMark? = null
    private var cachedLocation: GeoMarker? = null

    suspend fun findLocation(): GeoMarker? {

        val now = TimeSource.Monotonic.markNow()
        if (lastRequestMark != null && now.elapsedNow() < 2.seconds) {
            return cachedLocation
        }

        val rawLocation = provider.getCurrentLocation(LocationAccuracy.HIGH) ?: return null

        return when (val result = geocoder.getAddress(lat = rawLocation.latitude, lng = rawLocation.longitude)) {
            is ApiResult.Success -> {
                val finalMarker = mapToGeoMarker(result.data)

                cachedLocation = finalMarker
                lastRequestMark = TimeSource.Monotonic.markNow()

                finalMarker
            }
            is ApiResult.Error -> {
                result.exception?.printStackTrace()
                null
            }
        }
    }


}


@Composable
fun rememberGeoLocation(agentName: String, languageCode: String = "en"): GeoLocation = remember { GeoLocation(email = agentName, languageCode = languageCode) }

