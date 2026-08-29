package com.getair.design.player

import android.content.Context
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.getair.app.ui.model.IptvLiveInfo
import com.getair.app.ui.model.MediaItem
import com.getair.app.ui.player.AndroidPlayerUiController
import com.getair.app.ui.player.AndroidPlayerUiHost
import com.getair.video.AndroidMedia3BackendFactory
import com.getair.video.AndroidMedia3VideoPlayer
import com.getair.video.AudioTrack
import com.getair.video.PlaybackKind
import com.getair.video.PlaybackSource
import com.getair.video.PlaybackState
import com.getair.video.PlayerCapabilities
import com.getair.video.SubtitleTrack
import com.getair.video.VideoTrack
import kotlinx.coroutines.flow.StateFlow

/** Opens only the local synthetic fixture; canonical UI never sees its URI or Media3. */
class DesignAndroidPlayerUiHost(context: Context) : AndroidPlayerUiHost {
    private val factory = AndroidMedia3BackendFactory(context.applicationContext)
    override val isAvailable: Boolean = true

    @Composable
    override fun rememberController(item: MediaItem): AndroidPlayerUiController {
        val player = remember(item.id) { factory.createAndroidPlayer() }
        val controller = remember(player) { DesignAndroidPlayerUiController(player) }
        LaunchedEffect(item.id, player) {
            runCatching {
                player.open(
                    PlaybackSource(
                        uri = "asset:///air-player-test.mkv",
                        title = item.title,
                        kindHint = if (item.info is IptvLiveInfo) PlaybackKind.Live else PlaybackKind.OnDemand,
                    ),
                )
            }
        }
        return controller
    }
}

private class DesignAndroidPlayerUiController(
    private val player: AndroidMedia3VideoPlayer,
) : AndroidPlayerUiController {
    override val state: StateFlow<PlaybackState> = player.state
    override val capabilities: StateFlow<PlayerCapabilities> = player.capabilities
    override val audioTracks: StateFlow<List<AudioTrack>> = player.audioTracks
    override val subtitleTracks: StateFlow<List<SubtitleTrack>> = player.subtitleTracks
    override val videoTracks: StateFlow<List<VideoTrack>> = player.videoTracks

    @Composable
    override fun Surface(modifier: Modifier) {
        AndroidView(
            factory = { viewContext -> TextureView(viewContext).also(player::attach) },
            modifier = modifier,
        )
    }

    override fun play() = player.play()
    override fun pause() = player.pause()
    override fun seekTo(positionMillis: Long) {
        player.seekTo(positionMillis)
    }
    override fun selectAudioTrack(trackId: String?) {
        player.selectAudioTrack(trackId)
    }

    override fun selectSubtitleTrack(trackId: String?) {
        player.selectSubtitleTrack(trackId)
    }

    override fun selectVideoTrack(trackId: String?) {
        player.selectVideoTrack(trackId)
    }

    override fun close() = player.close()
}
