package com.getair.design.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.getair.design.model.IptvLiveInfo
import com.getair.design.model.MediaItem
import com.getair.design.model.clockRange
import com.getair.iptv.model.EpgProgramme
import com.getair.design.ui.IptvRoute
import com.getair.design.ui.components.PosterMediaCard
import com.getair.design.ui.focus.requestFocusSafely
import com.getair.design.ui.theme.AirBorderWidth
import com.getair.design.ui.theme.AirCardShape

@Composable
fun IptvScreen(
    onItemSelected: (MediaItem) -> Unit,
    liveChannels: List<MediaItem>,
    movies: List<MediaItem>,
    series: List<MediaItem>,
    sideNavigationFocusRequester: FocusRequester,
    contentEntryFocusRequester: FocusRequester,
    onContentFocused: (FocusRequester) -> Unit,
) {
    var route by remember { mutableStateOf(IptvRoute.Live) }
    val tabFocusRequesters = remember { List(IptvRoute.entries.size) { FocusRequester() } }
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 58.dp, top = 36.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TabRow(
                selectedTabIndex = IptvRoute.entries.indexOf(route),
                modifier = Modifier
                    .width(360.dp)
                    .onPreviewKeyEvent {
                        if (
                            route == IptvRoute.Live &&
                            it.key == Key.DirectionLeft &&
                            it.type == KeyEventType.KeyDown
                        ) {
                            sideNavigationFocusRequester.requestFocusSafely()
                        } else false
                    },
            ) {
                IptvRoute.entries.forEachIndexed { index, tab ->
                    val tabFocusRequester = if (tab == IptvRoute.Live) {
                        contentEntryFocusRequester
                    } else tabFocusRequesters[index]
                    Tab(
                        selected = tab == route,
                        onFocus = {
                            route = tab
                            onContentFocused(tabFocusRequester)
                        },
                        onClick = { route = tab },
                        modifier = Modifier
                            .focusRequester(tabFocusRequester)
                            .then(
                                if (tab == IptvRoute.Live) {
                                    Modifier.focusProperties { left = sideNavigationFocusRequester }
                                } else {
                                    Modifier
                                }
                            ),
                    ) {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = {}) { Text("Provider: All") }
            Spacer(Modifier.width(12.dp))
            OutlinedButton(onClick = {}) { Text("Filters") }
        }
        Spacer(Modifier.height(24.dp))
        when (route) {
            IptvRoute.Live -> LiveGuide(
                channels = liveChannels,
                onItemSelected = onItemSelected,
                sideNavigationFocusRequester = sideNavigationFocusRequester,
                onContentFocused = onContentFocused,
            )
            IptvRoute.Series -> VodGrid(
                series,
                onItemSelected,
                sideNavigationFocusRequester,
                onContentFocused,
            )
            IptvRoute.Movies -> VodGrid(
                movies,
                onItemSelected,
                sideNavigationFocusRequester,
                onContentFocused,
            )
        }
    }
}

@Composable
private fun LiveGuide(
    channels: List<MediaItem>,
    onItemSelected: (MediaItem) -> Unit,
    sideNavigationFocusRequester: FocusRequester,
    onContentFocused: (FocusRequester) -> Unit,
) {
    var selected by remember(channels) { mutableStateOf(channels.firstOrNull()) }
    Row(
        Modifier.fillMaxSize().padding(start = 64.dp, end = 58.dp).focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        LazyColumn(
            modifier = Modifier.width(250.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 48.dp),
        ) {
            itemsIndexed(channels, key = { _, channel -> channel.id }) { index, channel ->
                val info = channel.info as IptvLiveInfo
                val channelFocusRequester = remember(channel.id) { FocusRequester() }
                ListItem(
                    selected = channel == selected,
                    onClick = { onItemSelected(channel) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(channelFocusRequester)
                        .then(
                            if (index == 0) Modifier.focusProperties {
                                left = sideNavigationFocusRequester
                            } else Modifier
                        )
                        .onFocusChanged {
                            if (it.isFocused) {
                                selected = channel
                                onContentFocused(channelFocusRequester)
                            }
                        },
                    headlineContent = { Text("${info.channelNumber}  ${channel.title}") },
                    supportingContent = { Text(info.now.title, maxLines = 1) },
                    scale = ListItemDefaults.scale(focusedScale = 1f),
                    shape = ListItemDefaults.shape(AirCardShape),
                    colors = ListItemDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                )
            }
        }
        val selectedChannel = selected
        if (selectedChannel != null) {
            val info = selectedChannel.info as IptvLiveInfo
            Column(Modifier.weight(1f)) {
                Text("Now on ${selectedChannel.title}", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "${info.now.clockRange()}  •  ${info.group}  •  ${info.streamFormat}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    info.now.title,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    info.now.description.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    "Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 26.dp, bottom = 12.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ProgramCard(info.now, selected = true) { onItemSelected(selectedChannel) }
                    info.upcoming.forEach { ProgramCard(it, selected = false, onClick = {}) }
                }
            }
        }
    }
}

@Composable
private fun ProgramCard(program: EpgProgramme, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.width(165.dp).height(112.dp),
        shape = ClickableSurfaceDefaults.shape(AirCardShape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(AirBorderWidth, MaterialTheme.colorScheme.onSurface),
                shape = AirCardShape,
            )
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(program.clockRange(), style = MaterialTheme.typography.labelMedium)
            Text(program.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun VodGrid(
    items: List<MediaItem>,
    onItemSelected: (MediaItem) -> Unit,
    sideNavigationFocusRequester: FocusRequester,
    onContentFocused: (FocusRequester) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 64.dp, end = 58.dp, bottom = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
            val itemFocusRequester = remember(item.id) { FocusRequester() }
            PosterMediaCard(
                item = item,
                onClick = { onItemSelected(item) },
                modifier = Modifier
                    .focusRequester(itemFocusRequester)
                    .onFocusChanged { if (it.isFocused) onContentFocused(itemFocusRequester) }
                    .then(
                        if (index % 5 == 0) {
                            Modifier.focusProperties { left = sideNavigationFocusRequester }
                        } else Modifier
                    ),
            )
        }
    }
}
