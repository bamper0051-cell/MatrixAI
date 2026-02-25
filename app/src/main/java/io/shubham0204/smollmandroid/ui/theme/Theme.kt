/*
 * Matrix AI Theme
 * Always dark, always green.
 */

package io.shubham0204.smollmandroid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val matrixColorScheme = darkColorScheme(
    primary = MatrixGreen,
    onPrimary = MatrixOnPrimary,
    primaryContainer = MatrixGreenDim,
    onPrimaryContainer = MatrixGreen,
    secondary = MatrixSecondary,
    onSecondary = MatrixOnSecondary,
    secondaryContainer = MatrixSecondaryContainer,
    onSecondaryContainer = MatrixOnSecondaryContainer,
    tertiary = MatrixGreenDark,
    onTertiary = MatrixOnPrimary,
    tertiaryContainer = MatrixFaintGreen,
    onTertiaryContainer = MatrixGreen,
    error = MatrixError,
    onError = MatrixOnError,
    errorContainer = MatrixErrorContainer,
    onErrorContainer = MatrixError,
    background = MatrixBg,
    onBackground = MatrixOnBg,
    surface = MatrixSurface,
    onSurface = MatrixOnSurface,
    surfaceVariant = MatrixSurfaceVariant,
    onSurfaceVariant = MatrixOnSurfaceVariant,
    outline = MatrixOutline,
    outlineVariant = MatrixOutlineVariant,
    scrim = MatrixBlack,
    inverseSurface = MatrixGreen,
    inverseOnSurface = MatrixBlack,
    inversePrimary = MatrixBlack,
    surfaceDim = MatrixBlack,
    surfaceBright = MatrixSurfaceHigh,
    surfaceContainerLowest = MatrixBlack,
    surfaceContainerLow = MatrixBg,
    surfaceContainer = MatrixSurfaceContainer,
    surfaceContainerHigh = MatrixSurfaceHigh,
    surfaceContainerHighest = MatrixSurfaceHighest,
)

@Composable
fun SmolLMAndroidTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = matrixColorScheme,
        content = content,
        typography = AppTypography,
    )
}
