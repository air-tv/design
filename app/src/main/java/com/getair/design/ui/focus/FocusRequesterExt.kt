package com.getair.design.ui.focus

import androidx.compose.ui.focus.FocusRequester

/**
 * Requests focus without allowing a short-lived detach during recomposition to
 * crash the app. A false result lets the caller fall back to spatial focus.
 */
fun FocusRequester.requestFocusSafely(): Boolean =
    runCatching {
        requestFocus()
        true
    }.getOrDefault(false)
