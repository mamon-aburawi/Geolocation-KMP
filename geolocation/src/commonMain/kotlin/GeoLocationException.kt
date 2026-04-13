@PublishedApi
internal sealed class GeoLocationException(message: String) : Exception(message) {

    class PermissionMissing(message: String = "Location permissions are missing.") : GeoLocationException(message)

    class ServiceDisabled(message: String = "Location services/GPS are turned off.") : GeoLocationException(message)

    class HardwareTimeout(message: String = "Failed to retrieve location from hardware.") : GeoLocationException(message)

    class NetworkError(message: String) : GeoLocationException(message)


    class UnknownError(message: String): GeoLocationException(message)
}