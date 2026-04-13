package api.repository
import GeoLocationException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import model.*
import kotlin.time.Clock



@Serializable
internal data class IpApiResponse(
    val lat: Double? = null,
    val lon: Double? = null,
    val status: String? = null
)

internal class JvmGeolocationKmp : GeolocationKmp {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun getCurrentLocation(accuracy: LocationAccuracy): KmpLocation? {
        return try {
            val response: IpApiResponse = client.get("http://ip-api.com/json").body()

            if (response.status == "success" && response.lat != null && response.lon != null) {
                KmpLocation(
                    latitude = response.lat,
                    longitude = response.lon,
                    accuracy = 1000f, // City-level accuracy fallback
                    timestamp = Clock.System.now().toEpochMilliseconds()
                )
            } else {
                throw GeoLocationException.HardwareTimeout("Failed to resolve location from IP API.")
            }
        } catch (e: Exception) {
            // Catches Ktor exceptions (no internet, timeout) and wraps it
            if (e is GeoLocationException) throw e
            throw GeoLocationException.NetworkError("JVM Location Fallback Failed: ${e.message}")
        }
    }
}


actual fun getGeolocationProvider(): GeolocationKmp = JvmGeolocationKmp()


