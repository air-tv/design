package com.getair.design.ui.screens

import android.view.TextureView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.icons.filled.Check
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
import androidx.tv.material3.LocalContentColor
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
import com.getair.video.AudioTrack
import com.getair.video.HardwareAcceleration
import com.getair.video.MediaTrack
import com.getair.video.PlaybackKind
import com.getair.video.PlaybackSource
import com.getair.video.PlayerCapabilities
import com.getair.video.SubtitleTrack
import com.getair.video.VideoTrack

private enum class PlayerPanel { Audio, Subtitles, Quality, Advanced }

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
    val capabilities by player.capabilities.collectAsState()
    val primaryFocusRequester = remember { FocusRequester() }
    val panelFocusRequester = remember { FocusRequester() }
    val panelTriggerRequesters = remember {
        PlayerPanel.entries.associateWith { FocusRequester() }
    }
    var controlsVisible by remember(item.id) { mutableStateOf(true) }
    var activePanel by remember(item.id) { mutableStateOf<PlayerPanel?>(null) }
    var lastPanel by remember(item.id) { mutableStateOf<PlayerPanel?>(null) }

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
    LaunchedEffect(activePanel) {
        if (activePanel == null) {
            lastPanel?.let { panelTriggerRequesters.getValue(it).requestFocusSafely() }
        } else {
            panelFocusRequester.requestFocusSafely()
        }
    }
    BackHandler(enabled = activePanel != null) {
        activePanel = null
    }

    fun showPanel(panel: PlayerPanel) {
        lastPanel = panel
        activePanel = panel
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
                    TrackButton(
                        Icons.Default.Audiotrack,
                        "Audio",
                        audioTracks.selectedLabel(playbackState.selectedAudioTrackId, "Default"),
                        modifier = Modifier.focusRequester(panelTriggerRequesters.getValue(PlayerPanel.Audio)),
                        enabled = audioTracks.isNotEmpty(),
                    ) { showPanel(PlayerPanel.Audio) }
                    TrackButton(
                        Icons.Default.ClosedCaption,
                        "Subtitles",
                        subtitleTracks.selectedLabel(playbackState.selectedSubtitleTrackId, "Off"),
                        modifier = Modifier.focusRequester(panelTriggerRequesters.getValue(PlayerPanel.Subtitles)),
                    ) { showPanel(PlayerPanel.Subtitles) }
                    TrackButton(
                        Icons.Default.HighQuality,
                        "Quality",
                        videoTracks.selectedLabel(playbackState.selectedVideoTrackId, "Auto"),
                        modifier = Modifier.focusRequester(panelTriggerRequesters.getValue(PlayerPanel.Quality)),
                        enabled = videoTracks.isNotEmpty(),
                    ) { showPanel(PlayerPanel.Quality) }
                    PlayerIconButton(
                        Icons.Default.MoreHoriz,
                        "Advanced",
                        modifier = Modifier.focusRequester(panelTriggerRequesters.getValue(PlayerPanel.Advanced)),
                    ) { showPanel(PlayerPanel.Advanced) }
                }
            }
        }

        activePanel?.let { panel ->
            PlayerOptionsPanel(
                panel = panel,
                firstFocusRequester = panelFocusRequester,
                playbackState = playbackState,
                capabilities = capabilities,
                audioTracks = audioTracks,
                subtitleTracks = subtitleTracks,
                videoTracks = videoTracks,
                onSelectAudio = {
                    player.selectAudioTrack(it)
                    activePanel = null
                },
                onSelectSubtitle = {
                    player.selectSubtitleTrack(it)
                    activePanel = null
                },
                onSelectVideo = {
                    player.selectVideoTrack(it)
                    activePanel = null
                },
                onDismiss = { activePanel = null },
            )
        }
    }
}

@Composable
private fun PlayerIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = modifier) { Icon(icon, contentDescription = description) }
}

@Composable
private fun TrackButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonDefaults.shape(AirButtonShape),
        scale = ButtonDefaults.scale(scale = 1f),
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(7.dp))
        Text(if (value.isBlank()) label else "$label  $value")
    }
}

@Composable
private fun BoxScope.PlayerOptionsPanel(
    panel: PlayerPanel,
    firstFocusRequester: FocusRequester,
    playbackState: com.getair.video.PlaybackState,
    capabilities: PlayerCapabilities,
    audioTracks: List<AudioTrack>,
    subtitleTracks: List<SubtitleTrack>,
    videoTracks: List<VideoTrack>,
    onSelectAudio: (String?) -> Unit,
    onSelectSubtitle: (String?) -> Unit,
    onSelectVideo: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .width(430.dp)
            .background(Color(0xFF111214))
            .border(1.dp, Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 34.dp, vertical = 42.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = when (panel) {
                PlayerPanel.Audio -> "Audio track"
                PlayerPanel.Subtitles -> "Subtitles"
                PlayerPanel.Quality -> "Video quality"
                PlayerPanel.Advanced -> "Playback details"
            },
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = when (panel) {
                PlayerPanel.Audio -> "Choose the language or mix for this stream."
                PlayerPanel.Subtitles -> "Embedded and external tracks appear together."
                PlayerPanel.Quality -> "Choose an exposed video rendition."
                PlayerPanel.Advanced -> "Runtime facts reported by the active backend."
            },
            color = Color.White.copy(alpha = 0.58f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        when (panel) {
            PlayerPanel.Audio -> audioTracks.forEachIndexed { index, track ->
                PanelOption(
                    title = track.label,
                    detail = listOfNotNull(track.language, track.codec, track.channels?.let { "$it channels" })
                        .joinToString("  •  "),
                    selected = playbackState.selectedAudioTrackId == track.id,
                    modifier = if (index == 0) Modifier.focusRequester(firstFocusRequester) else Modifier,
                ) { onSelectAudio(track.id) }
            }
            PlayerPanel.Subtitles -> {
                PanelOption(
                    title = "Off",
                    detail = "No subtitles",
                    selected = playbackState.selectedSubtitleTrackId == null,
                    modifier = Modifier.focusRequester(firstFocusRequester),
                ) { onSelectSubtitle(null) }
                subtitleTracks.forEach { track ->
                    PanelOption(
                        title = track.label,
                        detail = listOfNotNull(track.language, track.format, "External".takeIf { track.external })
                            .joinToString("  •  "),
                        selected = playbackState.selectedSubtitleTrackId == track.id,
                    ) { onSelectSubtitle(track.id) }
                }
            }
            PlayerPanel.Quality -> videoTracks.forEachIndexed { index, track ->
                PanelOption(
                    title = track.label,
                    detail = listOfNotNull(
                        track.height?.let { "${it}p" },
                        track.codec,
                        track.bitrate?.let { "${it / 1_000_000.0} Mbps" },
                    ).joinToString("  •  "),
                    selected = playbackState.selectedVideoTrackId == track.id,
                    modifier = if (index == 0) Modifier.focusRequester(firstFocusRequester) else Modifier,
                ) { onSelectVideo(track.id) }
            }
            PlayerPanel.Advanced -> {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.focusRequester(firstFocusRequester).fillMaxWidth(),
                    shape = ButtonDefaults.shape(AirButtonShape),
                    scale = ButtonDefaults.scale(scale = 1f),
                ) {
                    Text("Done")
                }
                PlaybackFact("Timeline", playbackState.timeline?.kind?.label ?: "Opening")
                PlaybackFact("Hardware acceleration", capabilities.hardwareAcceleration.label)
                PlaybackFact("Movable surface", capabilities.supportsMovableSurface.onOff())
                PlaybackFact("Surface reattachment", capabilities.supportsSurfaceReattachment.onOff())
                PlaybackFact("Composited overlays", capabilities.supportsCompositedOverlays.onOff())
                PlaybackFact("HDR", capabilities.supportsHdr.onOff())
            }
        }
    }
}

@Composable
private fun PanelOption(
    title: String,
    detail: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = ButtonDefaults.shape(AirButtonShape),
        scale = ButtonDefaults.scale(scale = 1f),
        colors = ButtonDefaults.colors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.Transparent,
        ),
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (detail.isNotBlank()) {
                Text(
                    detail,
                    color = LocalContentColor.current.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (selected) Icon(Icons.Default.Check, contentDescription = "Selected")
    }
}

@Composable
private fun PlaybackFact(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.58f))
        Text(value)
    }
}

private fun Boolean.onOff(): String = if (this) "Supported" else "Unavailable"

private val PlaybackKind.label: String get() = when (this) {
    PlaybackKind.OnDemand -> "On demand"
    PlaybackKind.Live -> "Live"
    PlaybackKind.SeekableLive -> "DVR live"
}

private val HardwareAcceleration.label: String get() = when (this) {
    HardwareAcceleration.Unknown -> "Unknown"
    HardwareAcceleration.None -> "Software"
    HardwareAcceleration.Decode -> "Decode"
    HardwareAcceleration.DecodeAndRender -> "Decode + render"
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

private fun List<MediaTrack>.selectedLabel(selectedId: String?, fallback: String): String =
    firstOrNull { it.id == selectedId }?.label ?: fallback
