package com.example.catdex.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Ta palette Light
private val LightColorScheme = lightColorScheme(
    primary = Black,
    background = White,
    surface = White
)


// Ton thème
@Composable
fun CatdexTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
