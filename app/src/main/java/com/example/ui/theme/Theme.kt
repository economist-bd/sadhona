package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = NaturalPrimaryDark,
    secondary = NaturalSecondaryDark,
    tertiary = NaturalTertiaryDark,
    background = NaturalBackgroundDark,
    surface = NaturalSurfaceDark,
    onPrimary = Color(0xFF1A351F),
    onSecondary = Color(0xFF1C241D),
    onBackground = Color(0xFFECEBE4),
    onSurface = Color(0xFFECEBE4),
    outline = NaturalOutlineDark,
    secondaryContainer = NaturalSecondaryContainerDark,
    onSecondaryContainer = Color(0xFFECEBE4),
    surfaceVariant = NaturalSecondaryContainerDark,
    onSurfaceVariant = Color(0xFFB9B8A7)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = NaturalPrimaryLight,
    secondary = NaturalSecondaryLight,
    tertiary = NaturalTertiaryLight,
    background = NaturalBackgroundLight,
    surface = NaturalSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1C17),
    onSurface = Color(0xFF1C1C17),
    outline = NaturalOutlineLight,
    secondaryContainer = NaturalSecondaryContainerLight,
    onSecondaryContainer = Color(0xFF1C1C17),
    surfaceVariant = NaturalSecondaryContainerLight,
    onSurfaceVariant = Color(0xFF797869)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is disabled by default to show our custom Natural Tones design theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
