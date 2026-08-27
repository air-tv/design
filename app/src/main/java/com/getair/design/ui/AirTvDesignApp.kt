package com.getair.design.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.LocalContentColor
import com.getair.design.model.MediaItem
import com.getair.design.model.StaticData
import com.getair.design.ui.components.SideNavigation
import com.getair.design.ui.components.MainContentInset
import com.getair.design.ui.screens.HomeScreen
import com.getair.design.ui.screens.InfoScreen
import com.getair.design.ui.screens.IptvScreen
import com.getair.design.ui.screens.ProfilesScreen
import com.getair.design.ui.screens.SettingsScreen
import com.getair.design.ui.theme.AirTheme

@Composable
fun AirTvDesignApp(onExit: () -> Unit) {
    var oledMode by remember { mutableStateOf(false) }
    AirTheme(oledMode = oledMode) {
        var route by remember { mutableStateOf(AppRoute.Home) }
        var previousRoute by remember { mutableStateOf(AppRoute.Home) }
        var selectedItem by remember { mutableStateOf<MediaItem>(StaticData.projectHailMary) }
        var isSideNavigationFocused by remember { mutableStateOf(false) }
        var focusRequestEpoch by remember { mutableStateOf(0) }
        val contentEntryFocusRequester = remember { FocusRequester() }
        val selectedDestinationFocusRequester = remember { FocusRequester() }
        var lastContentFocusRequester by remember { mutableStateOf<FocusRequester?>(null) }

        fun showInfo(item: MediaItem) {
            selectedItem = item
            previousRoute = route
            route = AppRoute.Info
        }

        BackHandler {
            when (route) {
                AppRoute.Info -> route = previousRoute
                else -> when {
                    !isSideNavigationFocused -> {
                        focusRequestEpoch += 1
                    }
                    route != AppRoute.Home -> route = AppRoute.Home
                    else -> onExit()
                }
            }
        }

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            start = when (route) {
                                AppRoute.Home, AppRoute.Info -> 0.dp
                                else -> MainContentInset
                            }
                        )
                ) {
                    when (route) {
                        AppRoute.Home -> HomeScreen(
                            ::showInfo,
                            selectedDestinationFocusRequester,
                            contentEntryFocusRequester,
                            onContentFocused = { lastContentFocusRequester = it },
                        )
                        AppRoute.Iptv -> IptvScreen(
                            ::showInfo,
                            selectedDestinationFocusRequester,
                            contentEntryFocusRequester,
                            onContentFocused = { lastContentFocusRequester = it },
                        )
                        AppRoute.Settings -> SettingsScreen(
                            selectedDestinationFocusRequester,
                            contentEntryFocusRequester,
                            onContentFocused = { lastContentFocusRequester = it },
                            oledMode = oledMode,
                            onOledModeChange = { oledMode = it },
                        )
                        AppRoute.Profiles -> ProfilesScreen(
                            selectedDestinationFocusRequester,
                            contentEntryFocusRequester,
                            onContentFocused = { lastContentFocusRequester = it },
                        )
                        AppRoute.Info -> InfoScreen(
                            selectedItem,
                            selectedDestinationFocusRequester,
                            contentEntryFocusRequester,
                            onContentFocused = { lastContentFocusRequester = it },
                        )
                    }
                }
                SideNavigation(
                    selectedRoute = if (route == AppRoute.Info) previousRoute else route,
                    onRouteSelected = { route = it },
                    focusRequestEpoch = focusRequestEpoch,
                    contentEntryFocusRequester = contentEntryFocusRequester,
                    restoreContentFocusRequester = lastContentFocusRequester,
                    forceRouteSelection = route == AppRoute.Info,
                    selectedDestinationFocusRequester = selectedDestinationFocusRequester,
                    requestInitialFocus = route != AppRoute.Info,
                    onFocusChanged = { isSideNavigationFocused = it },
                )
            }
        }
    }
}
