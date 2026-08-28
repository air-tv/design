package com.getair.design.ui.screens

import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import com.getair.design.model.IptvLiveInfo
import com.getair.design.model.MediaItem
import com.getair.design.model.clockRange
import com.getair.design.ui.components.ColorArtwork
import com.getair.design.ui.focus.requestFocusSafely
import com.getair.design.ui.theme.AirButtonShape
import com.getair.video.AndroidMedia3BackendFactory
import com.getair.video.PlaybackKind
import com.getair.video.PlaybackSource

@Composable
fun PlayerScreen(
    item: MediaItem,
    onBack: () -> Unit,
) {
    val liveInfo = item.info as? IptvLiveInfo
    val context = LocalContext.current
    val player = remember(context) { AndroidMedia3BackendFactory(context).createAndroidPlayer() }
    val playbackState by player.state.collectAsState()
    val audioTracks by player.audioTracks.collectAsState()
    val subtitleTracks by player.subtitleTracks.collectAsState()
    val videoTracks by player.videoTracks.collectAsState()
    val primaryFocusRequester = remember { FocusRequester() }
    var controlsVisible by remember(item.id) { mutableStateOf(true) }

    DisposableEffect(player) {
        onDispose { player.close() }
    }
    LaunchedEffect(item.id, player) {
        runCatching {
            player.open(
                PlaybackSource(
                    uri = "asset:///air-player-test.mkv",
                    title = item.title,
                    kindHint = if (liveInfo == null) PlaybackKind.OnDemand else PlaybackKind.Live,
                ),
            )
        }
        primaryFocusRequester.requestFocusSafely()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        ColorArtwork(
            palette = item.palette,
            modifier = Modifier.fillMaxSize(),
        )
        AndroidView(
            factory = { viewContext ->
                TextureView(viewContext).also(player::attach)
            },
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.18f),
                        0.48f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.96f),
                    )
                )
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(44.dp).size(48.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        if (liveInfo != null) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 46.dp, end = 52.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(8.dp).background(Color(0xFFFF4B4B), CircleShape))
                Text("LIVE", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp))
            }
        }

        if (controlsVisible) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(start = 64.dp, end = 64.dp, bottom = 44.dp),
            ) {
                Text(
                    if (liveInfo != null) liveInfo.now.title else item.title,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    if (liveInfo != null) {
                        "${liveInfo.channelNumber}  ${liveInfo.channel.name}  •  ${liveInfo.now.clockRange()}"
                    } else {
                        "${playbackState.positionMillis.clock()}  /  " +
                            "${playbackState.timeline?.durationMillis?.clock() ?: "--:--"}"
                    },
                    color = Color.White.copy(alpha = 0.68f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp),
                )

                if (playbackState.timeline?.showSeekBar == true) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.24f), CircleShape),
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(playbackState.progressFraction())
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                } else {
                    Text(
                        "Up next  ${liveInfo?.upcoming?.firstOrNull()?.title ?: "No guide data"}",
                        color = Color.White.copy(alpha = 0.68f),
                        modifier = Modifier.padding(top = 18.dp),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (liveInfo == null) {
                        PlayerIconButton(Icons.Default.Replay10, "Back 10 seconds") {
                            player.seekTo(playbackState.positionMillis - 10_000)
                        }
                    }
                    Button(
                        onClick = {
                            controlsVisible = true
                            if (playbackState.playWhenReady) player.pause() else player.play()
                        },
                        modifier = Modifier.focusRequester(primaryFocusRequester),
                        shape = ButtonDefaults.shape(AirButtonShape),
                        scale = ButtonDefaults.scale(scale = 1f),
                    ) {
                        Icon(
                            if (playbackState.playWhenReady) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (playbackState.playWhenReady) "Pause" else "Play")
                    }
                    if (liveInfo == null) {
                        PlayerIconButton(Icons.Default.FastForward, "Forward") {
                            player.seekTo(playbackState.positionMillis + 30_000)
                        }
                    }

                    Spacer(Modifier.weight(1f))
                    TrackButton(Icons.Default.Audiotrack, "Audio", audioTracks.selectedLabel("Default"))
                    TrackButton(Icons.Default.ClosedCaption, "Subtitles", subtitleTracks.selectedLabel("Off"))
                    TrackButton(Icons.Default.HighQuality, "Quality", videoTracks.selectedLabel("Auto"))
                    PlayerIconButton(Icons.Default.MoreHoriz, "Advanced") {}
                }
            }
        }
    }
}

@Composable
private fun PlayerIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) { Icon(icon, contentDescription = description) }
}

@Composable
private fun TrackButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    OutlinedButton(
        onClick = {},
        shape = ButtonDefaults.shape(AirButtonShape),
        scale = ButtonDefaults.scale(scale = 1f),
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(7.dp))
        Text(if (value.isBlank()) label else "$label  $value")
    }
}

private fun Long.clock(): String {
    val totalSeconds = (coerceAtLeast(0) / 1_000)
    val hours = totalSeconds / 3_600
    val minutes = (totalSeconds % 3_600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

private fun com.getair.video.PlaybackState.progressFraction(): Float {
    val duration = timeline?.durationMillis ?: return 0f
    if (duration <= 0) return 0f
    return (positionMillis.toDouble() / duration).coerceIn(0.0, 1.0).toFloat()
}

private fun List<com.getair.video.MediaTrack>.selectedLabel(fallback: String): String =
    firstOrNull { it.isDefault }?.label ?: firstOrNull()?.label ?: fallback
