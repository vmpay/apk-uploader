package eu.vmpay.apk.uploader.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class AdbManager {
    /**
     * Opens a file picker dialog for a desktop application and returns the selected file path.
     * This implementation is specific to a JVM desktop environment.
     *
     * @return The absolute path of the selected file, or null if no file was selected.
     */
    fun openFilePickerAndGetPath(): String? {
        val fileChooser = JFileChooser().apply {
            dialogTitle = "Select APK File to Install"
            fileFilter = FileNameExtensionFilter("APK Files (*.apk)", "apk")
        }

        val result = fileChooser.showOpenDialog(null)

        return if (result == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile.absolutePath
        } else {
            null
        }
    }

    /**
     * Checks if the ADB command is installed and available in the system's PATH.
     *
     * @return true if ADB is found, false otherwise.
     */
    fun isAdbInstalled(): Boolean {
        return try {
            val process = ProcessBuilder("adb", "version")
                .redirectErrorStream(true)
                .start()
            process.waitFor()
            val exitCode = process.exitValue()
            exitCode == 0
        } catch (e: Exception) {
            println("An error occurred while checking ADB installation.")
            e.printStackTrace()
            false
        }
    }

    /**
     * Executes the `adb devices` command and returns a list of connected device serials.
     *
     * @return A [List] of [String]s, where each string is a device serial number.
     * Returns an empty list if no devices are found or an error occurs.
     */
    fun getConnectedDevices(): Map<String, String> {
        val devices = mutableMapOf<String, String>()
        try {
            // Use ProcessBuilder to execute the adb devices command.
            val process = ProcessBuilder("adb", "devices")
                .redirectErrorStream(true)
                .start()

            // Read the output from the command.
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            // Skip the first line, which is usually "List of devices attached".
            reader.readLine()

            while (reader.readLine().also { line = it } != null) {
                // Each device is on its own line, formatted as "<serial>\tdevice"
                // We split the line by the tab character and take the first part.
                val parts = line!!.split("\t")
                if (parts.size >= 2) {
                    val serial = parts[0].trim()
                    val state = parts[1].trim()
                    if (serial.isNotEmpty() && state.isNotEmpty()) {
                        devices[serial] = state
                    }
                }
            }

            process.waitFor()

        } catch (e: Exception) {
            println("An error occurred while running the 'adb devices' command.")
            e.printStackTrace()
            throw UploaderException(UploaderError.ADB_NOT_FOUND)
        }
        return devices
    }

    /**
     * Pushes and installs an APK file on a connected Android device using a single, direct ADB command.
     *
     * This version is more reliable as it avoids the intermediate 'push' step that can sometimes fail
     * to make the file available for the 'install' command.
     *
     * @param apkFilePath The local path to the APK file on your computer.
     * @param deviceSerial The serial number of the target device.
     */
    fun pushAndInstallApk(
        apkFilePath: String,
        deviceSerial: String,
    ): Flow<Int> = flow {
        emit(1)
        val apkFile = File(apkFilePath)
        if (!apkFile.exists()) {
            println("Error: The file '$apkFilePath' does not exist.")
            throw UploaderException(UploaderError.FILE_NOT_FOUND)
        }

        try {
            println("Installing '$apkFilePath' on device '$deviceSerial'...")
            emit(33)

            // Direct ADB install command
            val installProcess = ProcessBuilder(
                "adb", "-s", deviceSerial, "install", "-r", apkFilePath
            )
                .redirectErrorStream(true)
                .start()

            println("Waiting for install to complete...")
            emit(66)
            val installOutput = installProcess.inputStream.bufferedReader().use { it.readText() }
            installProcess.waitFor()
            println("Install command output:\n$installOutput")
            if (installProcess.exitValue() == 0) {
                println("APK installed successfully! 🎉")
                emit(100)
            } else {
                println("Failed to install APK. Check the output for details.")
                throw UploaderException(
                    if (installOutput.contains("not found") && installOutput.contains("adb: device"))
                        UploaderError.DEVICE_NOT_FOUND else UploaderError.UNKNOWN
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("An error occurred while running the ADB commands.")
            throw e as? UploaderException ?: UploaderException(UploaderError.UNKNOWN)
        }
    }
}