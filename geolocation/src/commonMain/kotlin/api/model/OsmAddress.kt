package api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OsmAddress(
    // Specific Landmarks (POIs)
    val amenity: String? = null,
    val building: String? = null,
    val shop: String? = null,
    val historic: String? = null,
    val tourism: String? = null,
    val office: String? = null,

    // Hyper-local
    @SerialName("house_number") val houseNumber: String? = null,
    val road: String? = null,
    val pedestrian: String? = null,
    val neighbourhood: String? = null,
    val suburb: String? = null,
    val quarter: String? = null,

    // City & Regional
    val city: String? = null,
    val town: String? = null,
    val village: String? = null,
    val municipality: String? = null,
    val county: String? = null,
    @SerialName("state_district") val stateDistrict: String? = null,
    val state: String? = null,
    val region: String? = null,

    // National
    val country: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    @SerialName("postcode") val postalCode: String? = null
) {
    // Professional fallback logic: Nominatim changes the key based on population/area type
    val safeCityName: String
        get() = city ?: town ?: village ?: municipality ?: ""

    val safeStreet: String
        get() = road ?: pedestrian ?: ""

    val safeNeighbourhood: String
        get() = neighbourhood ?: quarter ?: ""

    val safePointOfInterest: String
        get() = amenity ?: building ?: shop ?: historic ?: tourism ?: office ?: ""
}