package com.nipuna.safeme.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SafeMeColors = lightColorScheme(
    primary = SafeBlue,
    secondary = SafeBlueDark,
    background = SafeBackground
)

@Composable
fun SafeMeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SafeMeColors,
        content = content
    )
}
