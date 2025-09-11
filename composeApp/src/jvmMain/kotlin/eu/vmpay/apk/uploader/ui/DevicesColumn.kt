package eu.vmpay.apk.uploader.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import apkuploader.composeapp.generated.resources.Res
import apkuploader.composeapp.generated.resources.adb_not_found
import apkuploader.composeapp.generated.resources.adb_not_found_instruction_1
import apkuploader.composeapp.generated.resources.adb_not_found_instruction_2
import apkuploader.composeapp.generated.resources.adb_not_found_instruction_3
import apkuploader.composeapp.generated.resources.device_empty_list
import apkuploader.composeapp.generated.resources.device_not_found
import apkuploader.composeapp.generated.resources.select_device
import eu.vmpay.apk.uploader.AdbDevice
import eu.vmpay.apk.uploader.domain.UploaderError
import eu.vmpay.apk.uploader.domain.UploaderException
import org.jetbrains.compose.resources.stringResource
import java.awt.Desktop
import java.net.URI

@Composable
fun DevicesColumn(
    isEnabled: Boolean,
    devices: List<AdbDevice>,
    selectedDevice: AdbDevice?,
    error: UploaderException?,
    onDeviceSelected: (AdbDevice) -> Unit,
    onRefreshClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.select_device))
                IconButton(
                    onClick = onRefreshClick,
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        }
        items(devices.size) { index ->
            val device = devices[index]
            DeviceRow(device, device == selectedDevice, isEnabled = isEnabled) {
                onDeviceSelected(it)
            }
        }
        if (devices.isEmpty()) {
            item {
                Text(stringResource(Res.string.device_empty_list))
            }
        }
        if (error != null) {
            item {
                val errorMessage =
                    if (error.error == UploaderError.DEVICE_NOT_FOUND) stringResource(Res.string.device_not_found)
                    else if (error.error == UploaderError.ADB_NOT_FOUND) stringResource(Res.string.adb_not_found)
                    else error.error.toString()
                Text(errorMessage, color = MaterialTheme.colors.error)
                val annotatedLinkString = buildAnnotatedString {
                    append(stringResource(Res.string.adb_not_found_instruction_1))
                    pushStringAnnotation(
                        tag = "URL",
                        annotation = stringResource(Res.string.adb_not_found_instruction_2)
                    )
                    withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                        append(stringResource(Res.string.adb_not_found_instruction_3))
                    }
                    pop()
                    append(".")
                }

                ClickableText(
                    text = annotatedLinkString,
                    onClick = { offset ->
                        annotatedLinkString.getStringAnnotations(
                            tag = "URL",
                            start = offset,
                            end = offset
                        )
                            .firstOrNull()?.let {
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                                        .isSupported(Desktop.Action.BROWSE)
                                ) {
                                    Desktop.getDesktop().browse(URI(it.item))
                                }
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun DeviceRow(
    adbDevice: AdbDevice,
    isSelected: Boolean,
    isEnabled: Boolean,
    onDeviceSelected: (AdbDevice) -> Unit,
) {
    Row(
        modifier = if (isEnabled && adbDevice.state == "device") Modifier.clickable {
            onDeviceSelected(
                adbDevice
            )
        } else Modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Text(
            "${adbDevice.serial}\n${adbDevice.state}",
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}