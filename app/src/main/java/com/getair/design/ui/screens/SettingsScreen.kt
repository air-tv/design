/*
 * Two-column settings structure adapted from Android TV Samples:
 * JetStreamCompose/ProfileScreen.kt and TvMaterialCatalog/ListsScreen.kt.
 */
package com.getair.design.ui.screens

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.SwitchDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import com.getair.design.ui.focus.requestFocusSafely
import com.getair.design.ui.theme.AirCardShape

private enum class SettingsSection(val label: String, val icon: ImageVector) {
    Playback("Playback", Icons.Default.PlayCircle),
    Subtitles("Subtitles & audio", Icons.Default.ClosedCaption),
    Appearance("Appearance", Icons.Default.ColorLens),
    Sources("Sources", Icons.Default.Storage),
    Advanced("Advanced", Icons.Default.Build),
    About("About", Icons.Default.Info),
}

@Composable
fun SettingsScreen(
    sideNavigationFocusRequester: FocusRequester,
    contentEntryFocusRequester: FocusRequester,
    onContentFocused: (FocusRequester) -> Unit,
    oledMode: Boolean,
    onOledModeChange: (Boolean) -> Unit,
) {
    var section by remember { mutableStateOf(SettingsSection.Playback) }
    val sectionFocusRequesters = remember { List(SettingsSection.entries.size) { FocusRequester() } }
    val panelEntryFocusRequesters = remember { List(SettingsSection.entries.size) { FocusRequester() } }
    Row(Modifier.fillMaxSize().padding(horizontal = 58.dp, vertical = 18.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.32f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .focusGroup(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SettingsSection.entries.forEachIndexed { index, item ->
                val itemFocusRequester = if (item == SettingsSection.Playback) {
                    contentEntryFocusRequester
                } else sectionFocusRequesters[index]
                ListItem(
                    selected = item == section,
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(itemFocusRequester)
                        .focusProperties { left = sideNavigationFocusRequester }
                        .onPreviewKeyEvent {
                            if (it.key == Key.DirectionRight && it.type == KeyEventType.KeyDown) {
                                panelEntryFocusRequesters[index].requestFocusSafely()
                            } else {
                                false
                            }
                        }
                        .onFocusChanged {
                            if (it.isFocused) {
                                section = item
                                onContentFocused(itemFocusRequester)
                            }
                        },
                    leadingContent = { Icon(item.icon, contentDescription = null) },
                    headlineContent = { Text(item.label) },
                    scale = ListItemDefaults.scale(focusedScale = 1f),
                    shape = ListItemDefaults.shape(AirCardShape),
                    colors = ListItemDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                )
            }
        }
        SettingsPanel(
            section = section,
            oledMode = oledMode,
            onOledModeChange = onOledModeChange,
            panelEntryFocusRequester = panelEntryFocusRequesters[section.ordinal],
            modifier = Modifier.weight(1f).padding(start = 52.dp),
        )
    }
}

@Composable
private fun SettingsPanel(
    section: SettingsSection,
    oledMode: Boolean,
    onOledModeChange: (Boolean) -> Unit,
    panelEntryFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        Text(section.label, style = MaterialTheme.typography.headlineSmall)
        when (section) {
            SettingsSection.Playback -> {
                SettingValue("Autoplay next episode", "On", toggle = true, focusRequester = panelEntryFocusRequester)
                SettingValue("Preferred stream quality", "Auto")
                SettingValue("Live TV buffer", "Balanced")
                SettingValue("Resume playback", "Ask")
            }
            SettingsSection.Subtitles -> {
                SettingValue("Subtitles", "On", toggle = true, focusRequester = panelEntryFocusRequester)
                SettingValue("Preferred subtitle language", "English")
                SettingValue("Preferred audio language", "Original")
                SettingValue("Subtitle appearance", "System default")
            }
            SettingsSection.Appearance -> {
                SettingValue(
                    title = "OLED black",
                    value = if (oledMode) "On" else "Off",
                    toggle = true,
                    checked = oledMode,
                    onCheckedChange = onOledModeChange,
                    focusRequester = panelEntryFocusRequester,
                )
                SettingValue("Reduce motion", "Off", toggle = true, initial = false)
                SettingValue("Poster density", "Comfortable")
                SettingValue("Show content ratings", "On", toggle = true)
            }
            SettingsSection.Sources -> {
                SettingValue("Stremio addons", "3 installed", focusRequester = panelEntryFocusRequester)
                SettingValue("IPTV providers", "1 connected")
                SettingValue("Refresh catalogs", "Every 6 hours")
                SettingValue("Local network sources", "Off")
            }
            SettingsSection.Advanced -> {
                SettingValue("Hardware decoding", "On", toggle = true, focusRequester = panelEntryFocusRequester)
                SettingValue("Decoder policy", "Prefer platform")
                SettingValue("Network timeout", "15 seconds")
                SettingValue("Maximum addon response", "10 MiB")
                SettingValue("Diagnostics overlay", "Off", toggle = true, initial = false)
            }
            SettingsSection.About -> {
                SettingValue("Air TV Design", "0.1.0", focusRequester = panelEntryFocusRequester)
                SettingValue("UI source", "Android TV samples")
                SettingValue("Compose for TV", "Material 3")
                SettingValue("Build", "Static design data")
            }
        }
    }
}

@Composable
private fun SettingValue(
    title: String,
    value: String,
    toggle: Boolean = false,
    initial: Boolean = true,
    checked: Boolean? = null,
    onCheckedChange: (Boolean) -> Unit = {},
    focusRequester: FocusRequester? = null,
) {
    var internalChecked by remember(title) { mutableStateOf(initial) }
    val currentChecked = checked ?: internalChecked
    fun updateChecked(value: Boolean) {
        if (checked == null) internalChecked = value
        onCheckedChange(value)
    }
    ListItem(
        selected = false,
        onClick = { if (toggle) updateChecked(!currentChecked) },
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (focusRequester != null) Modifier.focusRequester(focusRequester)
                else Modifier
            )
            .padding(top = 16.dp),
        headlineContent = { Text(title, style = MaterialTheme.typography.titleMedium) },
        trailingContent = if (toggle) {
            {
                Switch(
                    checked = currentChecked,
                    onCheckedChange = ::updateChecked,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        } else {
            { Text(value, style = MaterialTheme.typography.labelLarge) }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        ),
        shape = ListItemDefaults.shape(AirCardShape),
        scale = ListItemDefaults.scale(focusedScale = 1f),
    )
}
