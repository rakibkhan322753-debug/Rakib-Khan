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

private val LightColorScheme = lightColorScheme(
  primary = ZawitcoBlue,
  onPrimary = Color.White,
  primaryContainer = ZawitcoLightBlue,
  onPrimaryContainer = Color.Black,
  secondary = ZawitcoOrange,
  onSecondary = Color.White,
  secondaryContainer = ZawitcoLightOrange,
  onSecondaryContainer = Color.Black,
  tertiary = EmeraldGreen,
  onTertiary = Color.White,
  background = BackgroundLight,
  onBackground = Color.Black,
  surface = SurfaceLight,
  onSurface = Color.Black,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color.Black,
  outline = Slate200,
  outlineVariant = Slate400
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF38A1EA),
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF003C66),
  onPrimaryContainer = Color(0xFFBCE1FA),
  secondary = Color(0xFFFF8B4A),
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF7A2900),
  onSecondaryContainer = Color(0xFFFFD5BE),
  tertiary = Color(0xFF34D399),
  onTertiary = Color.Black,
  background = Color(0xFF0F172A),
  onBackground = Color(0xFFF8FAFC),
  surface = Color(0xFF1E293B),
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF334155),
  onSurfaceVariant = Color(0xFFE2E8F0),
  outline = Color(0xFF475569),
  outlineVariant = Color(0xFF64748B)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve brand identity
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
