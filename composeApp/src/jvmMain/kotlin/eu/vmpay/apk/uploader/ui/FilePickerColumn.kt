package eu.vmpay.apk.uploader.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import apkuploader.composeapp.generated.resources.Res
import apkuploader.composeapp.generated.resources.file_not_found
import apkuploader.composeapp.generated.resources.pick
import apkuploader.composeapp.generated.resources.pick_apk_file
import eu.vmpay.apk.uploader.domain.UploaderError
import eu.vmpay.apk.uploader.domain.UploaderException
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilePickerColumn(
    isEnabled: Boolean,
    path: String,
    onPick: () -> Unit,
    error: UploaderException?
) {
    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(Res.string.pick_apk_file))
        Text(path)
        if (error != null) {
            val errorMessage =
                if (error.error == UploaderError.FILE_NOT_FOUND) stringResource(Res.string.file_not_found) else error.toString()
            Text(errorMessage, color = MaterialTheme.colors.error)
        }
        Button(
            enabled = isEnabled,
            onClick = {
                Napier.v("FilePickerColumn onClick")
                onPick()
            }) {
            Text(stringResource(Res.string.pick))
        }
    }
}
