package eu.vmpay.apk.uploader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import apkuploader.composeapp.generated.resources.Res
import apkuploader.composeapp.generated.resources.proceed
import eu.vmpay.apk.uploader.ui.DevicesColumn
import eu.vmpay.apk.uploader.ui.FilePickerColumn
import eu.vmpay.apk.uploader.ui.ProgressColumn
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
) {
    Napier.v("Hello napier")
    MaterialTheme {
        Scaffold(
            modifier = Modifier.padding(4.dp)
        ) {
            val uiState by viewModel.uiState.collectAsState()

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilePickerColumn(
                    isEnabled = !uiState.isLoading,
                    path = uiState.path,
                    error = uiState.pathError,
                    onPick = viewModel::onFilePickClick,
                )
                HorizontalDivider(thickness = 1.dp)
                DevicesColumn(
                    isEnabled = !uiState.isLoading,
                    devices = uiState.devices,
                    selectedDevice = uiState.selectedDevice,
                    error = uiState.devicesError,
                    onDeviceSelected = viewModel::onDeviceSelected,
                    onRefreshClick = viewModel::onRefreshClick,
                )
                HorizontalDivider(thickness = 1.dp)
                Button(
                    enabled = with(uiState) {
                        !isLoading && selectedDevice != null && path.isNotEmpty()
                    },
                    onClick = viewModel::onProceedClick
                ) {
                    Text(stringResource(Res.string.proceed))
                }
                ProgressColumn(
                    isLoading = uiState.isLoading,
                    message = uiState.message,
                )
            }
        }
    }
}