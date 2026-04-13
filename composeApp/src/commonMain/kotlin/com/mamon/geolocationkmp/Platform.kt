package com.mamon.geolocationkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform