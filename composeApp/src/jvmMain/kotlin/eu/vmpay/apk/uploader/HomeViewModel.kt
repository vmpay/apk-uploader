package eu.vmpay.apk.uploader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.vmpay.apk.uploader.domain.AdbManager
import eu.vmpay.apk.uploader.domain.UploaderError
import eu.vmpay.apk.uploader.domain.UploaderException
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            path = "",
            pathError = null,
            devices = listOf(),
            selectedDevice = null,
            devicesError = null,
            isLoading = false,
            message = "1. Choose APK file\n2. Choose device\n3. Click Proceed"
        ),
    )
    val uiState = _uiState.asStateFlow()
    private val adbManager = AdbManager()

    init {
        onRefreshClick()
    }

    fun onFilePickClick() {
        val path = adbManager.openFilePickerAndGetPath()
        if (!path.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    path = path,
                    pathError = null,
                )
            }
        }
    }

    fun onDeviceSelected(adbDevice: AdbDevice) {
        _uiState.update {
            it.copy(
                selectedDevice = adbDevice,
            )
        }
    }

    fun onRefreshClick() {
        Napier.v { "ViewModel onRefreshClick" }
        if (!adbManager.isAdbInstalled()) {
            _uiState.update {
                it.copy(
                    devicesError = UploaderException(UploaderError.ADB_NOT_FOUND),
                )
            }
            return
        }
        try {
            val devices = adbManager.getConnectedDevices().map {
                AdbDevice(
                    serial = it.key,
                    state = it.value,
                )
            }
            _uiState.update {
                it.copy(
                    devices = devices,
                    selectedDevice = null,
                )
            }
        } catch (uploaderException: UploaderException) {
            _uiState.update {
                it.copy(devicesError = uploaderException)
            }
        }
    }

    fun onProceedClick() {
        with(_uiState.value) {
            if (path.isNotEmpty() && selectedDevice != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        adbManager.pushAndInstallApk(
                            apkFilePath = path,
                            deviceSerial = selectedDevice.serial,
                        ).collect { progress ->
                            val isLoading = progress in 0 until 100
                            val message = when (progress) {
                                in 0 until 100 -> {
                                    "Uploading and installing $progress%"
                                }

                                100 -> {
                                    "APK installed successfully! 🎉"
                                }

                                else -> {
                                    "Failed to install APK. Check the output for details."
                                }
                            }
                            _uiState.update {
                                it.copy(
                                    isLoading = isLoading,
                                    message = message,
                                    pathError = null,
                                    devicesError = null,
                                )
                            }
                        }
                    } catch (uploaderException: UploaderException) {
                        when (uploaderException.error) {
                            UploaderError.UNKNOWN -> _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    message = uploaderException.toString(),
                                )
                            }

                            UploaderError.ADB_NOT_FOUND,
                            UploaderError.DEVICE_NOT_FOUND -> _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    devicesError = uploaderException,
                                    message = "",
                                )
                            }

                            UploaderError.FILE_NOT_FOUND -> _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    pathError = uploaderException,
                                    message = "",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class HomeUiState(
    val path: String,
    val pathError: UploaderException?,
    val devices: List<AdbDevice>,
    val selectedDevice: AdbDevice?,
    val devicesError: UploaderException?,
    val isLoading: Boolean = false,
    val message: String,
)