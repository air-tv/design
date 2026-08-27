/*
 * Adapted from Android TV Samples: JetStreamCompose.
 * Copyright 2023 Google LLC. Licensed under the Apache License, Version 2.0.
 */
package com.getair.design.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.getair.design.R

@Composable
private fun airColorScheme(oledMode: Boolean) = darkColorScheme(
        primary = colorResource(R.color.primary),
        onPrimary = colorResource(R.color.onPrimary),
        primaryContainer = colorResource(R.color.primaryContainer),
        onPrimaryContainer = colorResource(R.color.onPrimaryContainer),
        secondary = colorResource(R.color.secondary),
        onSecondary = colorResource(R.color.onSecondary),
        secondaryContainer = colorResource(R.color.secondaryContainer),
        onSecondaryContainer = colorResource(R.color.onSecondaryContainer),
        tertiary = colorResource(R.color.tertiary),
        onTertiary = colorResource(R.color.onTertiary),
        tertiaryContainer = colorResource(R.color.tertiaryContainer),
        onTertiaryContainer = colorResource(R.color.onTertiaryContainer),
        background = if (oledMode) Color.Black else colorResource(R.color.background),
        onBackground = colorResource(R.color.onBackground),
        surface = if (oledMode) Color.Black else colorResource(R.color.surface),
        onSurface = colorResource(R.color.onSurface),
        surfaceVariant = if (oledMode) Color(0xFF171717) else colorResource(R.color.surfaceVariant),
        onSurfaceVariant = colorResource(R.color.onSurfaceVariant),
        error = colorResource(R.color.error),
        onError = colorResource(R.color.onError),
        errorContainer = colorResource(R.color.errorContainer),
        onErrorContainer = colorResource(R.color.onErrorContainer),
        border = colorResource(R.color.border),
    )

@Composable
fun AirTheme(
    oledMode: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = airColorScheme(oledMode),
        typography = Typography,
        content = content,
    )
}
