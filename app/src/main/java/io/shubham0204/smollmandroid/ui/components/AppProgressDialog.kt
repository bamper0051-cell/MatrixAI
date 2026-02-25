package io.shubham0204.smollmandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.shubham0204.smollmandroid.ui.theme.MatrixGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixDarkBg
import io.shubham0204.smollmandroid.ui.theme.MatrixDarkGreen

private val progressDialogVisibleState = mutableStateOf(false)
private val progressDialogText = mutableStateOf("")
private val progressDialogTitle = mutableStateOf("")

@Composable
fun AppProgressDialog() {
    val isVisible by remember { progressDialogVisibleState }
    if (isVisible) {
        Surface {
            Dialog(onDismissRequest = { /* Progress dialogs are non-cancellable */ }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                MatrixDarkBg,
                                shape = RoundedCornerShape(8.dp),
                            ),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                    ) {
                        Text(
                            text = progressDialogTitle.value,
                            color = MatrixGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        MatrixLoadingAnimation(
                            label = progressDialogTitle.value.ifBlank { "PROCESSING" },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = progressDialogText.value,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            color = MatrixDarkGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}

fun setProgressDialogText(message: String) {
    progressDialogText.value = message
}

fun setProgressDialogTitle(title: String) {
    progressDialogTitle.value = title
}

fun showProgressDialog() {
    progressDialogVisibleState.value = true
}

fun hideProgressDialog() {
    progressDialogVisibleState.value = false
}
