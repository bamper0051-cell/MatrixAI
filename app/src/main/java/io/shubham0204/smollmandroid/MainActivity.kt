package io.shubham0204.smollmandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.shubham0204.smollmandroid.llm.ModelsRepository
import io.shubham0204.smollmandroid.ui.components.MatrixRainBackground
import io.shubham0204.smollmandroid.ui.screens.chat.ChatActivity
import io.shubham0204.smollmandroid.ui.screens.model_download.DownloadModelActivity
import io.shubham0204.smollmandroid.ui.theme.MatrixDarkBg
import io.shubham0204.smollmandroid.ui.theme.MatrixGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixLightGreen
import io.shubham0204.smollmandroid.ui.theme.SmolLMAndroidTheme
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val modelsRepository by inject<ModelsRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmolLMAndroidTheme {
                MatrixSplashScreen {
                    if (modelsRepository.getAvailableModelsList().isEmpty()) {
                        Intent(this@MainActivity, DownloadModelActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    } else {
                        Intent(this@MainActivity, ChatActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatrixSplashScreen(onFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    LaunchedEffect(Unit) {
        delay(2500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatrixDarkBg)
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        MatrixRainBackground(
            columnCount = 25,
            speedFactor = 0.8f,
            opacity = 0.4f,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "MATRIX",
                color = MatrixGreen.copy(alpha = pulse),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 12.sp,
            )

            Text(
                text = "[ AI ]",
                color = MatrixLightGreen.copy(alpha = pulse),
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 8.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Enter the Matrix",
                color = Color(0xFF00E676).copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "v1.0.0",
                color = Color(0xFF2E7D32),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}
