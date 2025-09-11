package eu.vmpay.apk.uploader

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Apk Uploader",
    ) {
        HomeScreen()
    }
}