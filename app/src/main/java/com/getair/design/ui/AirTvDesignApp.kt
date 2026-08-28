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
import com.getair.core.household.ProfilePreferences
import com.getair.design.model.MediaItem
import com.getair.design.model.StaticData
import com.getair.design.ui.components.SideNavigation
import com.getair.design.ui.components.MainContentInset
import com.getair.design.ui.screens.HomeScreen
import com.getair.design.ui.screens.InfoScreen
import com.getair.design.ui.screens.IptvScreen
import com.getair.design.ui.screens.ProfilesScreen
import com.getair.design.ui.screens.PlayerScreen
import com.getair.design.ui.screens.SettingsScreen
import com.getair.design.ui.theme.AirTheme

@Composable
fun AirTvDesignApp(onExit: () -> Unit) {
    var householdState by remember { mutableStateOf(StaticData.householdState) }
    AirTheme(oledMode = householdState.deviceSettings.oledBlack) {
        var route by remember { mutableStateOf(AppRoute.Home) }
        var previousRoute by remember { mutableStateOf(AppRoute.Home) }
        var selectedItem by remember { mutableStateOf<MediaItem>(StaticData.projectHailMary) }
        var isSideNavigationFocused by remember { mutableStateOf(false) }
        var focusRequestEpoch by remember { mutableStateOf(0) }
        val contentEntryFocusRequester = remember { FocusRequester() }
        val selectedDestinationFocusRequester = remember { FocusRequester() }
        var lastContentFocusRequester by remember { mutableStateOf<FocusRequester?>(null) }
        val selectedPreferences = householdState.selectedProfileId
            ?.let(householdState.profilePreferences::get)
            ?: ProfilePreferences()

        fun showInfo(item: MediaItem) {
            selectedItem = item
            previousRoute = route
            route = AppRoute.Info
        }

        fun showPlayer(item: MediaItem) {
            selectedItem = item
            route = AppRoute.Player
        }

        BackHandler {
            when (route) {
                AppRoute.Info -> route = previousRoute
                AppRoute.Player -> route = AppRoute.Info
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
                                AppRoute.Home, AppRoute.Info, AppRoute.Player -> 0.dp
                                else -> MainContentInset
                            }
                        )
                ) {
                    when (route) {
                        AppRoute.Home -> HomeScreen(
                            onItemSelected = ::showInfo,
                            continueWatching = StaticData.continueWatching(
                                requireNotNull(householdState.selectedProfileId),
                            ),
                            sideNavigationFocusRequester = selectedDestinationFocusRequester,
                            contentEntryFocusRequester = contentEntryFocusRequester,
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
                            deviceSettings = householdState.deviceSettings,
                            profilePreferences = selectedPreferences,
                            sourceState = StaticData.localSourceState,
                            onDeviceSettingsChange = { settings ->
                                if (settings != householdState.deviceSettings) {
                                    householdState = householdState.copy(
                                        deviceSettings = settings,
                                        revision = householdState.revision + 1,
                                    )
                                }
                            },
                            onProfilePreferencesChange = { preferences ->
                                householdState.selectedProfileId?.let { profileId ->
                                    if (preferences != householdState.profilePreferences[profileId]) {
                                        householdState = householdState.copy(
                                            profilePreferences = householdState.profilePreferences +
                                                (profileId to preferences),
                                            revision = householdState.revision + 1,
                                        )
                                    }
                                }
                            },
                        )
                        AppRoute.Profiles -> ProfilesScreen(
                            selectedDestinationFocusRequester,
                            contentEntryFocusRequester,
                            onContentFocused = { lastContentFocusRequester = it },
                            profiles = householdState.profiles,
                            selectedProfileId = householdState.selectedProfileId,
                            onProfileSelected = { profileId ->
                                if (profileId != householdState.selectedProfileId) {
                                    householdState = householdState.copy(
                                        selectedProfileId = profileId,
                                        revision = householdState.revision + 1,
                                    )
                                }
                                route = AppRoute.Home
                            },
                        )
                        AppRoute.Info -> InfoScreen(
                            selectedItem,
                            selectedDestinationFocusRequester,
                            contentEntryFocusRequester,
                            onPlay = ::showPlayer,
                            onContentFocused = { lastContentFocusRequester = it },
                        )
                        AppRoute.Player -> PlayerScreen(
                            item = selectedItem,
                            onBack = { route = AppRoute.Info },
                        )
                    }
                }
                if (route != AppRoute.Player) {
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
}
