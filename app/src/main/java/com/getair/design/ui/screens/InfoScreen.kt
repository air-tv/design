/*
 * Details composition adapted from Android TV Samples: JetStreamCompose/MovieDetails.kt.
 * Static branching demonstrates independent Stremio, IPTV live/EPG, and IPTV VOD data.
 */
package com.getair.design.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import com.getair.design.model.EpisodeInfo
import com.getair.design.model.IptvLiveInfo
import com.getair.design.model.IptvVodInfo
import com.getair.design.model.MediaItem
import com.getair.design.model.StremioInfo
import com.getair.design.model.clockRange
import com.getair.design.ui.components.ColorArtwork
import com.getair.design.ui.components.MediaFacts
import com.getair.design.ui.components.mediaKicker
import com.getair.design.ui.focus.requestFocusSafely
import com.getair.design.ui.theme.AirButtonShape

@Composable
fun InfoScreen(
    item: MediaItem,
    sideNavigationFocusRequester: FocusRequester,
    contentEntryFocusRequester: FocusRequester,
    onPlay: (MediaItem) -> Unit,
    onContentFocused: (FocusRequester) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 72.dp),
    ) {
        item {
            InfoHero(
                item,
                sideNavigationFocusRequester,
                contentEntryFocusRequester,
                onPlay,
                onContentFocused,
            )
        }
        item {
            when (val info = item.info) {
                is StremioInfo -> StremioDetails(info)
                is IptvLiveInfo -> LiveDetails(info)
                is IptvVodInfo -> IptvVodDetails(info)
            }
        }
    }
}

@Composable
private fun InfoHero(
    item: MediaItem,
    sideNavigationFocusRequester: FocusRequester,
    contentEntryFocusRequester: FocusRequester,
    onPlay: (MediaItem) -> Unit,
    onContentFocused: (FocusRequester) -> Unit,
) {
    LaunchedEffect(item.id) { contentEntryFocusRequester.requestFocusSafely() }
    Box(
        Modifier
            .fillMaxWidth()
            .height(380.dp)
            .focusRequester(contentEntryFocusRequester)
            .onFocusChanged {
                if (it.hasFocus) onContentFocused(contentEntryFocusRequester)
            }
            .onPreviewKeyEvent {
                if (it.key == Key.DirectionLeft && it.type == KeyEventType.KeyDown) {
                    sideNavigationFocusRequester.requestFocusSafely()
                } else false
            }
            .focusProperties { left = sideNavigationFocusRequester }
            .focusable()
    ) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
        ColorArtwork(
            palette = item.palette,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .aspectRatio(16f / 9f),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0f to MaterialTheme.colorScheme.surface,
                        0.32f to MaterialTheme.colorScheme.surface,
                        0.58f to MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                        0.82f to MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
                        1f to Color.Transparent,
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.68f to Color.Transparent,
                        1f to MaterialTheme.colorScheme.surface,
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(660.dp)
                .padding(start = 132.dp),
        ) {
            Text(
                mediaKicker(item),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                item.title,
                style = MaterialTheme.typography.headlineLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 7.dp),
            )
            Text(
                item.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 10.dp),
            )
            MediaFacts(item, Modifier.padding(top = 18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 20.dp)) {
                Button(
                    onClick = { onPlay(item) },
                    modifier = Modifier.focusProperties { left = sideNavigationFocusRequester },
                    shape = ButtonDefaults.shape(AirButtonShape),
                    scale = ButtonDefaults.scale(scale = 1f),
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (item.info is IptvLiveInfo) "Watch live" else "Watch")
                }
                OutlinedButton(onClick = {}, scale = ButtonDefaults.scale(scale = 1f)) {
                    Text("Choose source")
                }
            }
        }
    }
}

@Composable
private fun StremioDetails(info: StremioInfo) {
    Column(Modifier.padding(start = 132.dp, end = 58.dp, top = 28.dp, bottom = 28.dp)) {
        if (info.genres.isNotEmpty()) {
            Text(info.genres.joinToString("  •  "), color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp))
        }
        Text(info.description, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.width(720.dp).padding(top = 22.dp))
        if (info.director.isNotEmpty()) DetailPair("Director", info.director.joinToString())
        if (info.cast.isNotEmpty()) DetailPair("Cast", info.cast.joinToString())
        if (info.videos.isNotEmpty()) EpisodeRow(info.videos)
        Text(
            "Stremio meta resource",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}

@Composable
private fun LiveDetails(info: IptvLiveInfo) {
    Column(Modifier.padding(start = 132.dp, end = 58.dp, top = 28.dp, bottom = 28.dp)) {
        Text("Now", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 24.dp))
        Text(info.now.title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 8.dp))
        Text(info.now.clockRange(), color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp))
        Text(info.now.description.orEmpty(), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.width(720.dp).padding(top = 12.dp))
        Text("EPG", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 28.dp, bottom = 14.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(listOf(info.now) + info.upcoming) { program ->
                Column(
                    Modifier
                        .width(240.dp)
                        .height(118.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f), MaterialTheme.shapes.extraSmall)
                        .padding(16.dp)
                ) {
                    Text(program.clockRange(), style = MaterialTheme.typography.labelMedium)
                    Text(program.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        Text(
            "IPTV channel + XMLTV EPG",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}

@Composable
private fun IptvVodDetails(info: IptvVodInfo) {
    Column(Modifier.padding(start = 132.dp, end = 58.dp, top = 28.dp, bottom = 28.dp)) {
        Text(info.category, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp))
        Text(info.description, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.width(720.dp).padding(top = 22.dp))
        if (info.episodes.isNotEmpty()) EpisodeRow(info.episodes)
        Text(
            "IPTV provider VOD metadata",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}

@Composable
private fun DetailPair(label: String, value: String) {
    Row(Modifier.padding(top = 14.dp)) {
        Text("$label  ", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@Composable
private fun EpisodeRow(episodes: List<EpisodeInfo>) {
    Text("Episodes", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 30.dp, bottom = 14.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items(episodes) { episode ->
            Column(
                Modifier
                    .width(260.dp)
                    .height(124.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f), MaterialTheme.shapes.extraSmall)
                    .padding(16.dp)
            ) {
                Text("S${episode.season} E${episode.episode}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Text(episode.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 6.dp))
                Text(episode.overview, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}
