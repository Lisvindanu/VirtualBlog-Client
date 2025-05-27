package com.virtualsblog.Project.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,

    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSecondary,

    tertiary = Info,
    onTertiary = OnPrimary,

    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,

    background = Background,
    onBackground = OnBackground,

    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,

    outline = Border,
    outlineVariant = Border.copy(alpha = 0.5f),

    scrim = OnBackground.copy(alpha = 0.5f),
    inverseSurface = SurfaceDark,
    inverseOnSurface = OnSurfaceDark,
    inversePrimary = PrimaryDark
)

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryVariantDark,
    onPrimaryContainer = OnPrimaryDark,

    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryVariantDark,
    onSecondaryContainer = OnSecondaryDark,

    tertiary = Info,
    onTertiary = OnPrimaryDark,

    error = ErrorDark,
    onError = OnPrimaryDark,
    errorContainer = ErrorDark.copy(alpha = 0.2f),
    onErrorContainer = ErrorDark,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    outline = BorderDark,
    outlineVariant = BorderDark.copy(alpha = 0.5f),

    scrim = OnBackgroundDark.copy(alpha = 0.5f),
    inverseSurface = Surface,
    inverseOnSurface = OnSurface,
    inversePrimary = Primary
)

// Custom colors that extend Material3 theme
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val info: Color,
    val onInfo: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val border: Color
)

private val LightExtendedColors = ExtendedColors(
    success = Success,
    onSuccess = OnPrimary,
    warning = Warning,
    onWarning = OnPrimary,
    info = Info,
    onInfo = OnPrimary,
    cardBackground = CardBackground,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    border = Border
)

private val DarkExtendedColors = ExtendedColors(
    success = Success,
    onSuccess = OnPrimaryDark,
    warning = Warning,
    onWarning = OnPrimaryDark,
    info = Info,
    onInfo = OnPrimaryDark,
    cardBackground = CardBackgroundDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    textTertiary = TextTertiaryDark,
    border = BorderDark
)

// Composition local for extended colors
val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

@Composable
fun VirtualblogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to use custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = BlogShapes,
            content = content
        )
    }
}

// Extension property to access extended colors
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

// Convenience object for theme access
object BlogTheme {

    /**
     * Retrieve the current [ColorScheme] at the call site's position in the hierarchy.
     */
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    /**
     * Retrieve the current [ExtendedColors] at the call site's position in the hierarchy.
     */
    val extendedColors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.extendedColors

    /**
     * Retrieve the current [Typography] at the call site's position in the hierarchy.
     */
    val typography: androidx.compose.material3.Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    /**
     * Retrieve the current [BlogShapes] at the call site's position in the hierarchy.
     */
    val shapes: androidx.compose.material3.Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    /**
     * Helper properties for quick access to commonly used colors
     */
    object Colors {
        val primary: Color
            @Composable
            @ReadOnlyComposable
            get() = colorScheme.primary

        val secondary: Color
            @Composable
            @ReadOnlyComposable
            get() = colorScheme.secondary

        val background: Color
            @Composable
            @ReadOnlyComposable
            get() = colorScheme.background

        val surface: Color
            @Composable
            @ReadOnlyComposable
            get() = colorScheme.surface

        val error: Color
            @Composable
            @ReadOnlyComposable
            get() = colorScheme.error

        val success: Color
            @Composable
            @ReadOnlyComposable
            get() = extendedColors.success

        val warning: Color
            @Composable
            @ReadOnlyComposable
            get() = extendedColors.warning

        val info: Color
            @Composable
            @ReadOnlyComposable
            get() = extendedColors.info

        val textPrimary: Color
            @Composable
            @ReadOnlyComposable
            get() = extendedColors.textPrimary

        val textSecondary: Color
            @Composable
            @ReadOnlyComposable
            get() = extendedColors.textSecondary

        val textTertiary: Color
            @Composable
            @ReadOnlyComposable
            get() = extendedColors.textTertiary

        val border: Color
            @Composable
            @ReadOnlyComposable
            get() = extendedColors.border

        val cardBackground: Color
            @Composable
            @ReadOnlyComposable
            get() = extendedColors.cardBackground
    }

    /**
     * Helper properties for quick access to typography styles
     */
    object Text {
        val displayLarge
            @Composable
            @ReadOnlyComposable
            get() = typography.displayLarge

        val headlineLarge
            @Composable
            @ReadOnlyComposable
            get() = typography.headlineLarge

        val headlineMedium
            @Composable
            @ReadOnlyComposable
            get() = typography.headlineMedium

        val titleLarge
            @Composable
            @ReadOnlyComposable
            get() = typography.titleLarge

        val titleMedium
            @Composable
            @ReadOnlyComposable
            get() = typography.titleMedium

        val bodyLarge
            @Composable
            @ReadOnlyComposable
            get() = typography.bodyLarge

        val bodyMedium
            @Composable
            @ReadOnlyComposable
            get() = typography.bodyMedium

        val labelLarge
            @Composable
            @ReadOnlyComposable
            get() = typography.labelLarge

        // Custom typography styles
        val postTitle
            @Composable
            @ReadOnlyComposable
            get() = BlogTypography.postTitle

        val postContent
            @Composable
            @ReadOnlyComposable
            get() = BlogTypography.postContent

        val cardTitle
            @Composable
            @ReadOnlyComposable
            get() = BlogTypography.cardTitle

        val caption
            @Composable
            @ReadOnlyComposable
            get() = BlogTypography.caption

        val buttonText
            @Composable
            @ReadOnlyComposable
            get() = BlogTypography.buttonText
    }

    /**
     * Helper properties for quick access to shape styles
     */
    object Shapes {
        val small
            @Composable
            @ReadOnlyComposable
            get() = shapes.small

        val medium
            @Composable
            @ReadOnlyComposable
            get() = shapes.medium

        val large
            @Composable
            @ReadOnlyComposable
            get() = shapes.large

        // Custom shapes
        val textField
            @Composable
            @ReadOnlyComposable
            get() = CustomShapes.textField

        val button
            @Composable
            @ReadOnlyComposable
            get() = CustomShapes.button

        val card
            @Composable
            @ReadOnlyComposable
            get() = CustomShapes.card

        val postCard
            @Composable
            @ReadOnlyComposable
            get() = CustomShapes.postCard

        val bottomSheet
            @Composable
            @ReadOnlyComposable
            get() = CustomShapes.bottomSheet

        val topRounded
            @Composable
            @ReadOnlyComposable
            get() = CustomShapes.topRounded

        val bottomRounded
            @Composable
            @ReadOnlyComposable
            get() = CustomShapes.bottomRounded

        val fullRounded
            @Composable
            @ReadOnlyComposable
            get() = CustomShapes.fullRounded
    }
}

// Preview helpers for theme testing
@Composable
fun PreviewLightTheme(content: @Composable () -> Unit) {
    VirtualblogTheme(darkTheme = false, content = content)
}

@Composable
fun PreviewDarkTheme(content: @Composable () -> Unit) {
    VirtualblogTheme(darkTheme = true, content = content)
}