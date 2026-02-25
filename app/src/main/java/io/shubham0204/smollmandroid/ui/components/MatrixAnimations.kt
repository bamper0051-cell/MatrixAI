package io.shubham0204.smollmandroid.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.shubham0204.smollmandroid.ui.theme.MatrixGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixDarkGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixLightGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixBlack
import kotlin.math.cos
import kotlin.math.sin

/**
 * Matrix-style DOWNLOADING animation.
 * Displays a progress bar made of falling code blocks filling left to right,
 * with streaming binary data and percentage text.
 */
@Composable
fun MatrixDownloadAnimation(
    progress: Float = -1f, // -1 for indeterminate
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "download_anim")
    val sweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "download_sweep",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp),
    ) {
        // Animated progress bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        ) {
            val barHeight = size.height
            val barWidth = size.width

            // Background track
            drawRoundRect(
                color = Color(0xFF0A1A0A),
                size = Size(barWidth, barHeight),
            )

            // Progress fill with gradient
            val fillWidth = if (progress >= 0f) {
                barWidth * progress.coerceIn(0f, 1f)
            } else {
                // Indeterminate: bouncing bar
                val pos = sweep
                val segWidth = barWidth * 0.3f
                val startX = (barWidth + segWidth) * pos - segWidth
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MatrixDarkGreen,
                            MatrixGreen,
                            MatrixLightGreen,
                            MatrixGreen,
                            MatrixDarkGreen,
                            Color.Transparent,
                        ),
                        startX = startX,
                        endX = startX + segWidth,
                    ),
                    size = Size(barWidth, barHeight),
                )
                return@Canvas
            }

            // Determinate progress fill
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(MatrixDarkGreen, MatrixGreen, MatrixLightGreen),
                    endX = fillWidth,
                ),
                size = Size(fillWidth, barHeight),
            )

            // Glowing edge
            if (fillWidth > 2f) {
                drawRect(
                    color = MatrixLightGreen.copy(alpha = 0.8f),
                    topLeft = Offset(fillWidth - 3f, 0f),
                    size = Size(3f, barHeight),
                )
            }

            // Binary text overlay
            val binaryChars = "01"
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(120, 0, 255, 65)
                textSize = barHeight * 0.5f
                typeface = android.graphics.Typeface.MONOSPACE
            }
            val charW = paint.measureText("0")
            var x = (sweep * charW * 3) % (charW * 2) - charW
            while (x < barWidth) {
                val ch = binaryChars[kotlin.random.Random.nextInt(2)]
                drawContext.canvas.nativeCanvas.drawText(
                    ch.toString(), x, barHeight * 0.7f, paint,
                )
                x += charW
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status text
        val statusText = if (progress >= 0f) {
            "DOWNLOADING... ${(progress * 100).toInt()}%"
        } else {
            val dots = ".".repeat(((sweep * 3).toInt() % 4) + 1)
            "CONNECTING$dots"
        }

        Text(
            text = statusText,
            color = MatrixGreen,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Matrix-style LOADING animation.
 * Displays a circular spinner made of rotating code segments
 * with pulsing matrix characters around it.
 */
@Composable
fun MatrixLoadingAnimation(
    label: String = "LOADING MODEL",
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "loading_rotation",
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "loading_pulse",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp),
        ) {
            Canvas(modifier = Modifier.size(100.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2 - 8f

                // Outer rotating ring segments
                for (i in 0 until 8) {
                    val startAngle = rotation + i * 45f
                    val alpha = if (i % 2 == 0) pulse else 1f - pulse + 0.3f
                    rotate(startAngle, pivot = center) {
                        drawArc(
                            color = MatrixGreen.copy(alpha = alpha.coerceIn(0.2f, 1f)),
                            startAngle = 0f,
                            sweepAngle = 25f,
                            useCenter = false,
                            style = Stroke(width = 4f, cap = StrokeCap.Round),
                            topLeft = Offset(8f, 8f),
                            size = Size(radius * 2, radius * 2),
                        )
                    }
                }

                // Inner rotating characters
                val matrixChars = "MATRIX"
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(
                        (200 * pulse).toInt().coerceIn(80, 255), 0, 255, 65,
                    )
                    textSize = 14f
                    typeface = android.graphics.Typeface.MONOSPACE
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                for (i in matrixChars.indices) {
                    val angle = Math.toRadians((rotation + i * 60.0))
                    val charRadius = radius * 0.55f
                    val cx = center.x + cos(angle).toFloat() * charRadius
                    val cy = center.y + sin(angle).toFloat() * charRadius
                    drawContext.canvas.nativeCanvas.drawText(
                        matrixChars[i].toString(), cx, cy + 5f, paint,
                    )
                }

                // Center "AI" text
                val aiPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(255, 180, 255, 180)
                    textSize = 22f
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD,
                    )
                    textAlign = android.graphics.Paint.Align.CENTER
                    setShadowLayer(12f, 0f, 0f, android.graphics.Color.argb(200, 0, 255, 65))
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "AI", center.x, center.y + 8f, aiPaint,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = label,
            color = MatrixGreen.copy(alpha = pulse.coerceIn(0.5f, 1f)),
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Matrix-style INSTALLING animation.
 * Displays pseudo-terminal output with lines appearing one by one,
 * simulating package extraction and system installation.
 */
@Composable
fun MatrixInstallAnimation(
    currentStep: String = "",
    totalSteps: Int = 5,
    currentStepIndex: Int = 0,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "install_anim")
    val blink by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "cursor_blink",
    )
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan_line",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Terminal background
            drawRect(color = MatrixBlack)

            // Scan line effect
            val scanY = size.height * scanLine
            drawRect(
                color = MatrixGreen.copy(alpha = 0.05f),
                topLeft = Offset(0f, scanY - 20f),
                size = Size(size.width, 40f),
            )

            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(255, 0, 255, 65)
                textSize = 13f
                typeface = android.graphics.Typeface.MONOSPACE
            }
            val dimPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(150, 0, 180, 40)
                textSize = 13f
                typeface = android.graphics.Typeface.MONOSPACE
            }

            val lines = listOf(
                "root@matrix:~# installing matrix-ai-core",
                ">> Extracting system.dat... [OK]",
                ">> Verifying neural pathways... [OK]",
                ">> Loading AI kernel modules... [OK]",
                ">> Initializing inference engine...",
            )

            var y = 20f
            for (i in lines.indices) {
                if (i <= currentStepIndex) {
                    val p = if (i < currentStepIndex) dimPaint else paint
                    drawContext.canvas.nativeCanvas.drawText(lines[i], 8f, y, p)
                }
                y += 22f
            }

            // Blinking cursor
            if (blink > 0.5f) {
                drawRect(
                    color = MatrixGreen,
                    topLeft = Offset(8f + paint.measureText(
                        if (currentStepIndex < lines.size) lines[currentStepIndex] else ""
                    ), y - 32f),
                    size = Size(8f, 14f),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Step progress indicators
        val progress = if (totalSteps > 0) currentStepIndex.toFloat() / totalSteps else 0f
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        ) {
            drawRect(color = Color(0xFF0A1A0A))
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(MatrixDarkGreen, MatrixGreen),
                ),
                size = Size(size.width * progress, size.height),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (currentStep.isNotEmpty()) currentStep else "INSTALLING...",
            color = MatrixGreen,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
        )
    }
}
