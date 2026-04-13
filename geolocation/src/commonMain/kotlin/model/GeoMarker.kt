package model



data class GeoMarker(
    val fullAddress: String = "",
    val pointOfInterest: String = "",
    val houseNumber: String = "",
    val street: String = "",
    val neighbourhood: String = "",
    val suburb: String = "",
    val city: String = "",
    val county: String = "",
    val state: String = "",
    val country: String = "",
    val countryCode: String = "",
    val postalCode: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

