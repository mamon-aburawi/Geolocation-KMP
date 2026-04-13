package api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NominatimResponse(
    @SerialName("place_id")
    val placeId: Long? = null,
    val lat: String? = null,
    val lon: String? = null,
    @SerialName("display_name")
    val formattedAddress: String = "",
    val address: OsmAddress? = null
)
