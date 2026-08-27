package com.getair.design.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.getair.iptv.model.EpgProgramme
import com.getair.iptv.model.IptvCategory
import com.getair.iptv.model.IptvChannel
import com.getair.iptv.model.IptvMovie
import com.getair.iptv.model.IptvSeriesDetails
import com.getair.stremio.model.Meta

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
    val meta: Meta,
) : MediaInfo {
    override val title get() = meta.name
    override val description get() = meta.description.orEmpty()
    val type get() = meta.type
    val releaseInfo get() = meta.releaseInfo.orEmpty()
    val imdbRating get() = meta.imdbRating.orEmpty()
    val runtime get() = meta.runtime.orEmpty()
    val genres get() = meta.genres
    val director get() = meta.director
    val cast get() = meta.cast
    val videos get() = meta.videos.map {
        EpisodeInfo(
            season = it.season ?: 0,
            episode = it.episode ?: 0,
            title = it.title,
            overview = it.overview.orEmpty(),
        )
    }
}

@Immutable
data class EpisodeInfo(
    val season: Int,
    val episode: Int,
    val title: String,
    val overview: String,
)

@Immutable
data class IptvLiveInfo(
    val channel: IptvChannel,
    val category: IptvCategory,
    val streamFormat: String,
    val now: EpgProgramme,
    val upcoming: List<EpgProgramme>,
) : MediaInfo {
    override val title get() = channel.name
    override val description get() = now.description.orEmpty()
    val channelNumber get() = channel.number?.toInt()?.toString().orEmpty()
    val group get() = category.name
}

@Immutable
data class IptvVodInfo(
    val movie: IptvMovie? = null,
    val series: IptvSeriesDetails? = null,
    val categoryContract: IptvCategory,
    val durationLabel: String,
) : MediaInfo {
    init {
        require((movie == null) xor (series == null)) { "Exactly one IPTV VOD contract is required" }
    }

    override val title get() = movie?.name ?: requireNotNull(series).series.name
    override val description get() = movie?.plot ?: series?.series?.plot.orEmpty()
    val type get() = if (movie != null) MediaKind.Movie else MediaKind.Series
    val category get() = categoryContract.name
    val year get() = movie?.year ?: series?.series?.year.orEmpty()
    val rating get() = (movie?.rating ?: series?.series?.rating)?.toString().orEmpty()
    val duration get() = durationLabel
    val episodes get() = series?.episodes.orEmpty().map {
        EpisodeInfo(
            season = it.season.toInt(),
            episode = it.episode.toInt(),
            title = it.title,
            overview = it.plot.orEmpty(),
        )
    }
}

fun EpgProgramme.clockRange(): String =
    "${start.toString().substring(11, 16)}–${end?.toString()?.substring(11, 16) ?: "—"}"

@Immutable
data class Profile(
    val name: String,
    val initials: String,
    val palette: ArtworkPalette,
    val isKids: Boolean = false,
)
