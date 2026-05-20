package com.example.musculation

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CustomDarkColorScheme = darkColorScheme(
    background = PureBlack,
    surface = CardSurface,
    surfaceVariant = CardSurface,
    primary = CyanElectric,
    secondary = TealAccent,
    onBackground = PureWhite,
    onSurface = PureWhite,
    onSurfaceVariant = SlateGray,
    outline = SlateGray
)

@Composable
fun FlexTrackTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        dynamicDarkColorScheme(context)
    } else {
        CustomDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}