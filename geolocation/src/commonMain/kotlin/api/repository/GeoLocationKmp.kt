package api.repository

import model.KmpLocation
import model.LocationAccuracy



interface GeolocationKmp {
    suspend fun getCurrentLocation(accuracy: LocationAccuracy = LocationAccuracy.HIGH): KmpLocation?

}



expect fun getGeolocationProvider(): GeolocationKmp

