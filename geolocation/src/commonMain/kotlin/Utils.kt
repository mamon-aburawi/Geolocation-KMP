
import model.GeoMarker
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import api.model.NominatimResponse
import kotlin.coroutines.cancellation.CancellationException


internal const val TAG = "GeoLocation"

internal fun mapToGeoMarker(res: NominatimResponse): GeoMarker {
    val adder = res.address

    return GeoMarker(
        fullAddress = res.formattedAddress,
        pointOfInterest = adder?.safePointOfInterest ?: "",
        houseNumber = adder?.houseNumber ?: "",
        street = adder?.safeStreet ?: "",
        neighbourhood = adder?.safeNeighbourhood ?: "",
        suburb = adder?.suburb ?: "",
        city = adder?.safeCityName ?: "",
        county = adder?.county ?: adder?.stateDistrict ?: "",
        state = adder?.state ?: adder?.region ?: "",
        country = adder?.country ?: "",
        countryCode = adder?.countryCode?.uppercase() ?: "",
        postalCode = adder?.postalCode ?: "",
        latitude = res.lat?.toDoubleOrNull() ?: 0.0,
        longitude = res.lon?.toDoubleOrNull() ?: 0.0,

        )
}



internal suspend inline fun <reified T> safeApiCall(call: () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(call())
    } catch (e: ResponseException) {
        val statusCode = e.response.status.value

        if (statusCode == 429) {
            ApiResult.Error(statusCode = statusCode, exception = GeoLocationException.NetworkError("Too many requests. Please try again later."))
        } else {
            val description = e.response.status.description
            ApiResult.Error(statusCode = statusCode, exception = GeoLocationException.NetworkError("HTTP Error $statusCode: $description"))
        }

    } catch (e: HttpRequestTimeoutException) {
        ApiResult.Error(statusCode = null, exception = GeoLocationException.NetworkError("Request timed out"))
    } catch (e: IOException) {
        ApiResult.Error(statusCode = null, exception = GeoLocationException.NetworkError("Network error. Please check your connection."))
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        ApiResult.Error(statusCode = null, exception = GeoLocationException.UnknownError("An unexpected error occurred: ${e.message}"))
    }
}



//internal suspend inline fun <reified T> safeApiCall(call: () -> T): ApiResult<T> {
//    return try {
//        ApiResult.Success(call())
//    } catch (e: ResponseException) {
//        val statusCode = e.response.status.value
//        val description = e.response.status.description
//        ApiResult.Error(statusCode, "HTTP Error $statusCode: $description", e)
//    } catch (e: HttpRequestTimeoutException) {
//        ApiResult.Error(null, "Request timed out", e)
//    } catch (e: IOException) {
//        ApiResult.Error(null, "Network error. Please check your connection.", e)
//    } catch (e: Exception) {
//        ApiResult.Error(null, "An unexpected error occurred: ${e.message}", e)
//    }
//}


internal sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(
        val statusCode: Int? = null,
        val exception: GeoLocationException? = null
    ) : ApiResult<Nothing>()
}


internal fun generateRandomString(length: Int = 15): String {
    val allowedChars = ('A'..'Z') + ('a'..'z')

    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

