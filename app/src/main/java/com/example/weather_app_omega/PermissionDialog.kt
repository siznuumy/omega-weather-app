package com.example.weather_app_omega

//import androidx.compose.foundation.layout.RowScopeInstance.align
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun PermissionDialog(
//    permission: String,
//    onDismiss: () -> Unit,
    onGranted: () -> Unit,
//    modifier: Modifier
) {
    AlertDialog(
        onDismissRequest = {  },
        confirmButton = {
            onGranted()
        },
        title = {
            stringResource(id = R.string.permission)
        },
        text = {
            stringResource(id = R.string.permission_info)
        },
        modifier = Modifier.wrapContentHeight()
    )
}