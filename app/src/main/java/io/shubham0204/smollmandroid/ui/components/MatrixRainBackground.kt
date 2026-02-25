package io.shubham0204.smollmandroid.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

private val MATRIX_CHARS = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン" +
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#\$%^&*|<>[]{}ΑΒΓΔΩΨΞΦΠΘΛΣΥΖабвгдеж"

data class MatrixDrop(
    var col: Int,
    var row: Float,
    val speed: Float,
    val chars: MutableList<Char>,
    val maxLen: Int,
)

@Composable
fun MatrixRainBackground(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val fontSize = with(density) { 14.dp.toPx() }
    val colWidth = fontSize

    var drops by remember { mutableStateOf(listOf<MatrixDrop>()) }
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }
    var tick by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60)
            tick++
            drops = drops.map { drop ->
                val newRow = drop.row + drop.speed
                val newChars = drop.chars.toMutableList()
                // Add a new random char at head
                newChars.add(0, MATRIX_CHARS[Random.nextInt(MATRIX_CHARS.length)])
                if (newChars.size > drop.maxLen) newChars.removeLastOrNull()
                // Reset when off screen
                if (newRow * fontSize > canvasHeight + drop.maxLen * fontSize) {
                    drop.copy(row = -Random.nextFloat() * 20f, chars = mutableListOf())
                } else {
                    drop.copy(row = newRow, chars = newChars)
                }
            }
        }
    }

    val paint = remember {
        android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL
            textAlign = android.graphics.Paint.Align.LEFT
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (canvasWidth != size.width || canvasHeight != size.height) {
            canvasWidth = size.width
            canvasHeight = size.height
            val cols = (size.width / colWidth).toInt() + 1
            drops = List(cols) { col ->
                MatrixDrop(
                    col = col,
                    row = -Random.nextFloat() * 30f,
                    speed = 0.3f + Random.nextFloat() * 0.7f,
                    chars = mutableListOf(),
                    maxLen = 8 + Random.nextInt(20),
                )
            }
        }

        drawIntoCanvas { canvas ->
            drops.forEach { drop ->
                drop.chars.forEachIndexed { index, char ->
                    val x = drop.col * colWidth
                    val y = (drop.row - index) * fontSize

                    if (y < 0 || y > size.height) return@forEachIndexed

                    val alpha = when {
                        index == 0 -> 255  // Head: bright white-green
                        index < 3 -> (200 - index * 30).coerceAtLeast(0)
                        else -> ((255 - index * (200f / drop.maxLen))).toInt().coerceIn(0, 180)
                    }

                    val color = when {
                        index == 0 -> android.graphics.Color.argb(alpha, 180, 255, 180)
                        index < 3 -> android.graphics.Color.argb(alpha, 0, 255, 65)
                        else -> android.graphics.Color.argb(alpha, 0, 180, 40)
                    }

                    paint.color = color
                    paint.textSize = fontSize
                    canvas.nativeCanvas.drawText(char.toString(), x, y, paint)
                }
            }
        }
    }
}
