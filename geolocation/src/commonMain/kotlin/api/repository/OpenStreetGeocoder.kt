package api.repository

import ApiResult
import api.model.NominatimResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import safeApiCall





@PublishedApi
internal class OpenStreetGeocoder(
    val email: String,
    val languageCode: String = "en"
) {
    private val userAgent = "GeoLocationKmp/1.0 (Contact: $email)"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
    }

    suspend fun getAddress(
        lat: Double,
        lng: Double,
    ): ApiResult<NominatimResponse> {
        return safeApiCall(
            call = {
                httpClient.get("https://nominatim.openstreetmap.org/reverse") {
                    url {
                        parameters.append("format", "json")
                        parameters.append("lat", lat.toString())
                        parameters.append("lon", lng.toString())
                        parameters.append("zoom", "18")

                        // Optional but recommended for Nominatim:
                        // You can also pass it as a URL parameter to enforce the language
                        parameters.append("accept-language", languageCode)
                    }

                    header("User-Agent", userAgent)

                    header("Accept-Language", "$languageCode,en;q=0.5")
                }.body<NominatimResponse>()
            }
        )
    }
}


