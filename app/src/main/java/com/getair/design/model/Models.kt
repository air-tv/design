package com.getair.design.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ArtworkPalette(
    val start: Color,
    val middle: Color,
    val end: Color,
)

enum class MediaKind { Movie, Series, Live }

@Immutable
data class MediaItem(
    val id: String,
    val title: String,
    val description: String,
    val kind: MediaKind,
    val palette: ArtworkPalette,
    val info: MediaInfo,
    val progress: Float? = null,
)

sealed interface MediaInfo {
    val title: String
    val description: String
}

@Immutable
data class StremioInfo(
    override val title: String,
    override val description: String,
    val type: String,
    val releaseInfo: String,
    val imdbRating: String,
    val runtime: String,
    val genres: List<String>,
    val director: List<String>,
    val cast: List<String>,
    val videos: List<EpisodeInfo> = emptyList(),
) : MediaInfo

@Immutable
data class EpisodeInfo(
    val season: Int,
    val episode: Int,
    val title: String,
    val overview: String,
)

@Immutable
data class IptvLiveInfo(
    override val title: String,
    override val description: String,
    val channelNumber: String,
    val group: String,
    val streamFormat: String,
    val now: EpgProgram,
    val upcoming: List<EpgProgram>,
) : MediaInfo

@Immutable
data class IptvVodInfo(
    override val title: String,
    override val description: String,
    val type: MediaKind,
    val category: String,
    val year: String,
    val rating: String,
    val duration: String,
    val episodes: List<EpisodeInfo> = emptyList(),
) : MediaInfo

@Immutable
data class EpgProgram(
    val start: String,
    val end: String,
    val title: String,
    val description: String,
)

@Immutable
data class Profile(
    val name: String,
    val initials: String,
    val palette: ArtworkPalette,
    val isKids: Boolean = false,
)
