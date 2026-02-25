package io.shubham0204.smollmandroid.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.roundToInt
import kotlin.random.Random

private val matrixChars = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン0123456789ABCDEFMATRIXAI"

/**
 * Full-screen Matrix digital rain background animation.
 * Draws falling green Japanese katakana characters on black background.
 */
@Composable
fun MatrixRainBackground(
    modifier: Modifier = Modifier,
    columnCount: Int = 30,
    speedFactor: Float = 1f,
    opacity: Float = 1f,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "matrix_rain")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (100 / speedFactor).roundToInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rain_tick",
    )

    val drops = remember(columnCount) {
        IntArray(columnCount) { Random.nextInt(0, 40) }
    }

    val charCache = remember(columnCount) {
        Array(columnCount) { matrixChars[Random.nextInt(matrixChars.length)] }
    }

    // Update drops on each animation tick
    if (animProgress >= 0f) {
        for (i in drops.indices) {
            drops[i]++
            charCache[i] = matrixChars[Random.nextInt(matrixChars.length)]
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Color(0xFF0D0208).copy(alpha = 0.15f))
        val fontSize = size.width / columnCount
        drawMatrixColumns(drops, charCache, fontSize, opacity)
    }
}

private fun DrawScope.drawMatrixColumns(
    drops: IntArray,
    charCache: Array<Char>,
    fontSize: Float,
    opacity: Float,
) {
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.argb(
            (255 * opacity).roundToInt(), 0, 255, 65
        )
        textSize = fontSize
        typeface = android.graphics.Typeface.MONOSPACE
        isAntiAlias = true
    }

    val dimPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.argb(
            (100 * opacity).roundToInt(), 0, 180, 40
        )
        textSize = fontSize
        typeface = android.graphics.Typeface.MONOSPACE
        isAntiAlias = true
    }

    val brightPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.argb(
            (255 * opacity).roundToInt(), 180, 255, 180
        )
        textSize = fontSize
        typeface = android.graphics.Typeface.MONOSPACE
        isAntiAlias = true
        setShadowLayer(8f, 0f, 0f, android.graphics.Color.argb(200, 0, 255, 65))
    }

    val maxRows = (size.height / fontSize).roundToInt()

    for (i in drops.indices) {
        val x = i * fontSize
        val y = drops[i] * fontSize

        // Draw the bright head character
        drawContext.canvas.nativeCanvas.drawText(
            charCache[i].toString(),
            x,
            y,
            brightPaint,
        )

        // Draw trailing chars with decreasing brightness
        for (trail in 1..8) {
            val trailY = y - trail * fontSize
            if (trailY > 0) {
                val trailChar = matrixChars[Random.nextInt(matrixChars.length)]
                val trailPaint = if (trail < 3) paint else dimPaint
                drawContext.canvas.nativeCanvas.drawText(
                    trailChar.toString(),
                    x,
                    trailY,
                    trailPaint,
                )
            }
        }

        // Reset column when it goes off screen
        if (drops[i] * fontSize > size.height && Random.nextFloat() > 0.975f) {
            drops[i] = 0
        }
    }
}
