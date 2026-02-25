package io.shubham0204.smollmandroid.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.shubham0204.smollmandroid.ui.theme.MatrixGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixGreenDark
import io.shubham0204.smollmandroid.ui.theme.MatrixGreenDim
import io.shubham0204.smollmandroid.ui.theme.MatrixSurface
import io.shubham0204.smollmandroid.ui.theme.MatrixSurfaceContainer
import io.shubham0204.smollmandroid.ui.theme.MatrixOutline
import kotlinx.coroutines.delay

enum class ProgressAnimationType { DOWNLOAD, LOADING, INSTALLING }

private val progressDialogVisibleState = mutableStateOf(false)
private val progressDialogText = mutableStateOf("")
private val progressDialogTitle = mutableStateOf("")
private val progressDialogType = mutableStateOf(ProgressAnimationType.LOADING)

@Composable
fun AppProgressDialog() {
    val isVisible = progressDialogVisibleState.value
    if (!isVisible) return

    Dialog(onDismissRequest = { /* non-cancellable */ }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(MatrixSurface)
                .border(1.dp, MatrixOutline, RoundedCornerShape(4.dp))
        ) {
            // Matrix rain background behind dialog content
            MatrixRainBackground(modifier = Modifier.matchParentSize())

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color(0xCC010D01), RoundedCornerShape(4.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp),
            ) {
                // Title
                Text(
                    text = progressDialogTitle.value,
                    color = MatrixGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))

                when (progressDialogType.value) {
                    ProgressAnimationType.DOWNLOAD -> DownloadAnimation()
                    ProgressAnimationType.LOADING -> LoadingAnimation()
                    ProgressAnimationType.INSTALLING -> InstallingAnimation()
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = progressDialogText.value,
                    color = MatrixGreenDark,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// АНИМАЦИЯ 1: СКАЧИВАНИЕ
// Заполняющийся прогресс-бар с бегущими символами [====>    ]
// ─────────────────────────────────────────────
@Composable
private fun DownloadAnimation() {
    var progress by remember { mutableFloatStateOf(0f) }
    var statusLine by remember { mutableStateOf("ИНИЦИАЛИЗАЦИЯ...") }

    val phases = listOf(
        "ИНИЦИАЛИЗАЦИЯ...", "ПОДКЛЮЧЕНИЕ К СЕРВЕРУ...",
        "АВТОРИЗАЦИЯ...", "ЗАГРУЗКА ДАННЫХ...",
        "ЗАПИСЬ В ХРАНИЛИЩЕ...", "ПРОВЕРКА ЦЕЛОСТНОСТИ..."
    )

    LaunchedEffect(Unit) {
        var phaseIdx = 0
        while (true) {
            delay(40)
            progress = (progress + 0.008f).coerceAtMost(1f)
            if (progress >= (phaseIdx + 1) / phases.size.toFloat()) {
                phaseIdx = (phaseIdx + 1).coerceAtMost(phases.size - 1)
                statusLine = phases[phaseIdx]
            }
            if (progress >= 1f) { delay(500); progress = 0f; phaseIdx = 0 }
        }
    }

    val filled = (progress * 24).toInt()
    val bar = "[" + "=".repeat(filled) + ">".takeIf { filled < 24 }.orEmpty() +
            " ".repeat((23 - filled).coerceAtLeast(0)) + "]"

    Text(
        text = bar,
        color = MatrixGreen,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = "${(progress * 100).toInt()}%",
        color = MatrixGreen,
        fontFamily = FontFamily.Monospace,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = statusLine,
        color = MatrixGreenDark,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
    )
}

// ─────────────────────────────────────────────
// АНИМАЦИЯ 2: ЗАГРУЗКА МОДЕЛИ
// Вращающийся спиннер + пульсирующий текст
// ─────────────────────────────────────────────
@Composable
private fun LoadingAnimation() {
    val spinnerFrames = listOf("|", "/", "─", "\\", "|", "/", "─", "\\")
    var frameIdx by remember { mutableIntStateOf(0) }
    var dotCount by remember { mutableIntStateOf(0) }
    val loadLines = listOf(
        "> АКТИВАЦИЯ НЕЙРОСЕТИ",
        "> ЗАГРУЗКА ВЕСОВ МОДЕЛИ",
        "> НАСТРОЙКА ПАРАМЕТРОВ",
        "> ИНИЦИАЛИЗАЦИЯ КОНТЕКСТА",
        "> МАТРИЦА ГОТОВА",
    )
    var lineIdx by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(120)
            frameIdx = (frameIdx + 1) % spinnerFrames.size
            dotCount = (dotCount + 1) % 4
            lineIdx = (frameIdx / 2) % loadLines.size
        }
    }

    val infinite = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow",
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = spinnerFrames[frameIdx],
            color = MatrixGreen.copy(alpha = glowAlpha),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        )
        Spacer(Modifier.width(12.dp))
        Column {
            loadLines.forEachIndexed { i, line ->
                val lineColor = if (i <= lineIdx) MatrixGreen else MatrixGreenDim
                val lineWeight = if (i == lineIdx) FontWeight.Bold else FontWeight.Normal
                Text(
                    text = line + if (i == lineIdx) ".".repeat(dotCount) else "",
                    color = lineColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = lineWeight,
                    fontSize = 9.sp,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// АНИМАЦИЯ 3: УСТАНОВКА
// Строки появляются одна за другой (псевдо-распаковка)
// ─────────────────────────────────────────────
@Composable
private fun InstallingAnimation() {
    val installLines = listOf(
        "распаковка core.matrix........[OK]",
        "запись inference.engine.......[OK]",
        "настройка нейросети...........[OK]",
        "компиляция граматики..........[ . ]",
        "проверка зависимостей.........[ . ]",
        "финальная конфигурация........[///]",
    )
    var visibleLines by remember { mutableIntStateOf(1) }
    var cursorVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            if (visibleLines < installLines.size) visibleLines++
            else { delay(1000); visibleLines = 1 }
        }
    }
    LaunchedEffect(Unit) {
        while (true) { delay(500); cursorVisible = !cursorVisible }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        installLines.take(visibleLines).forEachIndexed { i, line ->
            val isLast = i == visibleLines - 1
            val displayLine = if (isLast && cursorVisible) "$line█" else line
            val color = when {
                line.contains("[OK]") -> MatrixGreen
                isLast -> MatrixGreenDark
                else -> MatrixGreenDim
            }
            Text(
                text = displayLine,
                color = color,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

// ─────────────────────────────────────────────
// PUBLIC API
// ─────────────────────────────────────────────
fun setProgressDialogText(message: String) {
    progressDialogText.value = message
}

fun setProgressDialogTitle(title: String) {
    progressDialogTitle.value = title
}

fun setProgressDialogType(type: ProgressAnimationType) {
    progressDialogType.value = type
}

fun showProgressDialog() {
    progressDialogVisibleState.value = true
}

fun hideProgressDialog() {
    progressDialogVisibleState.value = false
}
