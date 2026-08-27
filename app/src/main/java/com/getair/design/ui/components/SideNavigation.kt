package com.getair.design.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.SelectableSurfaceDefaults
import androidx.tv.material3.Surface
import com.getair.design.ui.AppRoute
import com.getair.design.ui.focus.requestFocusSafely
import kotlinx.coroutines.launch

val SideNavigationCollapsedWidth = 88.dp
val MainContentInset = 40.dp

private data class SideDestination(
    val route: AppRoute,
    val label: String,
    val icon: ImageVector,
)

private val profileDestination = SideDestination(AppRoute.Profiles, "Profiles", Icons.Default.People)

private val mainDestinations = listOf(
    SideDestination(AppRoute.Home, "Home", Icons.Default.Home),
    SideDestination(AppRoute.Iptv, "IPTV", Icons.Default.LiveTv),
    SideDestination(AppRoute.Settings, "Settings", Icons.Default.Settings),
)

private val allDestinations = listOf(profileDestination) + mainDestinations

@Composable
fun SideNavigation(
    selectedRoute: AppRoute,
    onRouteSelected: (AppRoute) -> Unit,
    focusRequestEpoch: Int,
    contentEntryFocusRequester: FocusRequester,
    restoreContentFocusRequester: FocusRequester?,
    forceRouteSelection: Boolean = false,
    selectedDestinationFocusRequester: FocusRequester,
    requestInitialFocus: Boolean = true,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val requesters = remember { List(allDestinations.size) { FocusRequester() } }

    fun enterContent(destination: SideDestination) {
        val sameRoute = destination.route == selectedRoute && !forceRouteSelection
        if (!sameRoute) onRouteSelected(destination.route)
        scope.launch {
            withFrameNanos { }
            val restored = if (sameRoute) {
                restoreContentFocusRequester?.let { requester ->
                    requester.requestFocusSafely()
                } ?: false
            } else {
                contentEntryFocusRequester.requestFocusSafely()
            }
            if (!restored) focusManager.moveFocus(FocusDirection.Right)
        }
    }

    LaunchedEffect(focusRequestEpoch, requestInitialFocus) {
        if (requestInitialFocus) selectedDestinationFocusRequester.requestFocusSafely()
    }

    Box(
        modifier = modifier
            .zIndex(3f)
            .width(SideNavigationCollapsedWidth)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .onFocusChanged { onFocusChanged(it.hasFocus) },
        contentAlignment = Alignment.TopStart,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SideNavigationItem(
                destination = profileDestination,
                selected = selectedRoute == profileDestination.route,
                requester = if (selectedRoute == profileDestination.route) {
                    selectedDestinationFocusRequester
                } else requesters[0],
                onClick = { enterContent(profileDestination) },
                onMoveRight = { enterContent(profileDestination) },
            )

            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                mainDestinations.forEachIndexed { index, destination ->
                    val selected = destination.route == selectedRoute
                    SideNavigationItem(
                        destination = destination,
                        selected = selected,
                        requester = if (selected) {
                            selectedDestinationFocusRequester
                        } else requesters[index + 1],
                        onClick = { enterContent(destination) },
                        onMoveRight = { enterContent(destination) },
                    )
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun SideNavigationItem(
    destination: SideDestination,
    selected: Boolean,
    requester: FocusRequester,
    onClick: () -> Unit,
    onMoveRight: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .focusRequester(requester)
            .onPreviewKeyEvent {
                if (it.key == Key.DirectionRight && it.type == KeyEventType.KeyDown) {
                    onMoveRight()
                    true
                } else {
                    false
                }
            }
            .onFocusChanged { focused = it.isFocused },
        shape = SelectableSurfaceDefaults.shape(CircleShape),
        colors = SelectableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = Color.Transparent,
            selectedContentColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.surface,
            focusedSelectedContainerColor = MaterialTheme.colorScheme.onSurface,
            focusedSelectedContentColor = MaterialTheme.colorScheme.surface,
            pressedSelectedContainerColor = MaterialTheme.colorScheme.onSurface,
            pressedSelectedContentColor = MaterialTheme.colorScheme.surface,
        ),
        scale = SelectableSurfaceDefaults.scale(focusedScale = 1f),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (selected && !focused) {
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
                        } else Color.Transparent,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(destination.icon, contentDescription = destination.label, modifier = Modifier.size(20.dp))
            }
        }
    }
}
