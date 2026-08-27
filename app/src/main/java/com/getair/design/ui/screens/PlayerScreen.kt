package com.getair.design.ui.screens

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
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import com.getair.video.PlaybackKind
import com.getair.video.PlaybackState
import com.getair.video.PlaybackStatus
import com.getair.video.PlaybackTimeline

@Composable
fun PlayerScreen(
    item: MediaItem,
    onBack: () -> Unit,
) {
    val liveInfo = item.info as? IptvLiveInfo
    val playbackState = remember(item.id) {
        PlaybackState(
            status = PlaybackStatus.Ready,
            playWhenReady = true,
            isPlaying = true,
            positionMillis = if (liveInfo == null) 3_798_000 else 0,
            timeline = if (liveInfo == null) {
                PlaybackTimeline(PlaybackKind.OnDemand, durationMillis = 9_420_000)
            } else {
                PlaybackTimeline(PlaybackKind.Live)
            },
        )
    }
    val primaryFocusRequester = remember { FocusRequester() }
    var controlsVisible by remember(item.id) { mutableStateOf(true) }

    LaunchedEffect(item.id) { primaryFocusRequester.requestFocusSafely() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        ColorArtwork(
            palette = item.palette,
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
                        "01:03:18  /  02:37:00"
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
                                .fillMaxWidth(0.41f)
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
                        PlayerIconButton(Icons.Default.Replay10, "Back 10 seconds")
                    }
                    Button(
                        onClick = { controlsVisible = true },
                        modifier = Modifier.focusRequester(primaryFocusRequester),
                        shape = ButtonDefaults.shape(AirButtonShape),
                        scale = ButtonDefaults.scale(scale = 1f),
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Pause")
                    }
                    if (liveInfo == null) {
                        PlayerIconButton(Icons.Default.FastForward, "Forward")
                    }

                    Spacer(Modifier.weight(1f))
                    TrackButton(Icons.Default.Audiotrack, "Audio", "English 5.1")
                    TrackButton(Icons.Default.ClosedCaption, "Subtitles", "English")
                    TrackButton(Icons.Default.HighQuality, "Quality", if (liveInfo != null) "Auto" else "4K")
                    PlayerIconButton(Icons.Default.MoreHoriz, "Advanced")
                }
            }
        }
    }
}

@Composable
private fun PlayerIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String) {
    IconButton(onClick = {}) { Icon(icon, contentDescription = description) }
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
