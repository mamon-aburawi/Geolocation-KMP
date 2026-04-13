package model


data class KmpLocation(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null, // Altitude & Climb
    val speed: Float? = null,     // Speed Tracking (m/s)
    val bearing: Float? = null,   // Heading & Compass
    val accuracy: Float? = null,
    val timestamp: Long
)