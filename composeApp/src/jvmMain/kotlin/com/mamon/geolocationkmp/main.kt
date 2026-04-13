package com.mamon.geolocationkmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GeolocationKmp",
    ) {
        App()
    }
}