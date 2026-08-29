package com.getair.design.fixtures

import androidx.compose.ui.graphics.Color
import com.getair.app.ui.model.ArtworkPalette
import com.getair.app.ui.model.IptvLiveInfo
import com.getair.app.ui.model.IptvVodInfo
import com.getair.app.ui.model.MediaItem
import com.getair.app.ui.model.MediaKind
import com.getair.app.ui.model.ProgrammeInfo
import com.getair.app.ui.model.StremioInfo
import com.getair.app.ui.model.TvPresentationSnapshot
import com.getair.core.history.ContinueWatchingState
import com.getair.core.history.OnDemandContentRef
import com.getair.core.history.WatchProgress
import com.getair.core.household.HouseholdProfile
import com.getair.core.household.HouseholdProfileId
import com.getair.core.household.HouseholdState
import com.getair.core.household.ProfilePreferences
import com.getair.core.source.LocalSourceId
import com.getair.core.source.LocalSourceKind
import com.getair.core.source.LocalSourceProfile
import com.getair.core.source.LocalSourceState
import com.getair.iptv.model.CategoryId
import com.getair.iptv.model.ChannelId
import com.getair.iptv.model.ChannelKind
import com.getair.iptv.model.EpgChannelId
import com.getair.iptv.model.EpisodeId
import com.getair.iptv.model.IptvChannelMetadata
import com.getair.iptv.model.IptvEpisodeMetadata
import com.getair.iptv.model.IptvMovieMetadata
import com.getair.iptv.model.IptvSeriesMetadata
import com.getair.iptv.model.IptvSourceKind
import com.getair.iptv.model.MovieId
import com.getair.iptv.model.SeriesId
import com.getair.stremio.model.Meta
import com.getair.stremio.model.Video
import kotlinx.datetime.Instant

object StaticData {
    private val blue = ArtworkPalette(Color(0xFF17243D), Color(0xFF365A8B), Color(0xFF91A9D2))
    private val rust = ArtworkPalette(Color(0xFF341B20), Color(0xFF8A3D35), Color(0xFFD68B53))
    private val violet = ArtworkPalette(Color(0xFF211A36), Color(0xFF594B87), Color(0xFFB6A1DA))
    private val moss = ArtworkPalette(Color(0xFF162621), Color(0xFF3F6957), Color(0xFF99B891))
    private val gold = ArtworkPalette(Color(0xFF2E2415), Color(0xFF84662C), Color(0xFFD7B35C))
    private val cyan = ArtworkPalette(Color(0xFF10272D), Color(0xFF247080), Color(0xFF83C8CE))
    private val plum = ArtworkPalette(Color(0xFF2B1728), Color(0xFF744566), Color(0xFFC996B3))
    private val slate = ArtworkPalette(Color(0xFF1E2227), Color(0xFF4B5967), Color(0xFFAAB5C0))

    val projectHailMary = MediaItem(
        id = "tt12042730",
        title = "Project Hail Mary",
        description = "A science teacher wakes up alone on a spaceship and uncovers a mission to save Earth's sun.",
        kind = MediaKind.Movie,
        palette = blue,
        info = StremioInfo(Meta(
            id = "tt12042730",
            type = "movie",
            name = "Project Hail Mary",
            description = "A science teacher wakes up alone on a spaceship. As his memory returns, he uncovers a mission to stop a mysterious substance killing Earth's sun, and realizes that an unexpected friendship may be the key.",
            releaseInfo = "2026",
            imdbRating = "8.2",
            runtime = "157 min",
            genres = listOf("Adventure", "Comedy", "Drama"),
            director = listOf("Phil Lord", "Christopher Miller"),
            cast = listOf("Ryan Gosling", "Sandra Hüller", "James Ortiz"),
        )),
    )

    val stremioMovies = listOf(
        projectHailMary,
        stremioMovie("tt37287335", "Obsession", "2026", "7.9", "109 min", rust, "Horror", "Romance", "Thriller"),
        stremioMovie("tt14173636", "The Invite", "2026", "7.8", "107 min", violet, "Comedy", "Drama", "Romance"),
        stremioMovie("tt26657236", "Backrooms", "2026", "6.8", "111 min", moss, "Horror", "Sci-Fi", "Thriller"),
        stremioMovie("tt29355505", "Toy Story 5", "2026", "7.4", "102 min", gold, "Animation", "Adventure", "Comedy"),
        stremioMovie("tt32565993", "The Sheep Detectives", "2026", "7.5", "109 min", cyan, "Comedy", "Family", "Mystery"),
    )

    val stremioSeries = listOf(
        stremioSeries("tt0944947", "The Northern Crown", violet),
        stremioSeries("tt2861424", "Parallel Lines", cyan),
        stremioSeries("tt0903747", "Glass District", rust),
        stremioSeries("tt7366338", "The Long Winter", blue),
        stremioSeries("tt0386676", "Office Hours", gold),
        stremioSeries("tt1475582", "Signal House", moss),
    )

    val continueWatchingState = ContinueWatchingState(
        entriesByProfile = mapOf(
            HouseholdProfileId("living-room") to listOf(
                watchProgress(projectHailMary, 42),
                watchProgress(stremioSeries.first(), 68),
                watchProgress(stremioMovies[3], 18),
                watchProgress(stremioSeries[2], 81),
            ),
            HouseholdProfileId("alex") to listOf(
                watchProgress(stremioMovies[1], 33),
                watchProgress(stremioSeries[1], 12),
            ),
            HouseholdProfileId("sam") to listOf(
                watchProgress(stremioSeries[3], 54),
                watchProgress(stremioMovies[5], 27),
            ),
            HouseholdProfileId("kids") to listOf(
                watchProgress(stremioMovies[4], 61),
                watchProgress(stremioSeries[4], 15),
            ),
        ),
    )

    fun continueWatching(profileId: HouseholdProfileId): List<MediaItem> =
        continueWatchingState.entriesByProfile.getValue(profileId)
        .mapNotNull { progress ->
            (stremioMovies + stremioSeries)
                .firstOrNull { item -> item.id == (progress.content as? OnDemandContentRef.Stremio)?.id }
                ?.copy(progress = progress.fraction)
        }

    val liveChannels = listOf(
        liveChannel("101", "World News", "News", blue, "The Evening Report", "18:00", "19:00"),
        liveChannel("118", "Metro Sports", "Sports", rust, "Wednesday Night Basketball", "18:30", "21:00"),
        liveChannel("204", "Science 24", "Documentary", cyan, "Deep Ocean Signals", "18:15", "19:15"),
        liveChannel("221", "Cinema One", "Movies", violet, "The Last Outpost", "17:45", "19:50"),
        liveChannel("305", "Kids Club", "Kids", gold, "Galaxy Friends", "18:40", "19:10"),
        liveChannel("410", "Local 8", "Local", moss, "City Desk", "18:00", "19:00"),
    )

    val iptvMovies = listOf(
        iptvVod("vod-1", "Midnight Circuit", MediaKind.Movie, "Action", "2025", "7.4", "112 min", rust),
        iptvVod("vod-2", "Quiet Orbit", MediaKind.Movie, "Science Fiction", "2024", "7.8", "126 min", blue),
        iptvVod("vod-3", "Green River", MediaKind.Movie, "Drama", "2023", "7.1", "98 min", moss),
        iptvVod("vod-4", "Second Light", MediaKind.Movie, "Mystery", "2025", "6.9", "104 min", gold),
        iptvVod("vod-5", "High Country", MediaKind.Movie, "Adventure", "2022", "7.6", "119 min", slate),
        iptvVod("vod-6", "After Hours", MediaKind.Movie, "Comedy", "2024", "7.0", "96 min", plum),
        iptvVod("vod-7", "Blue Room", MediaKind.Movie, "Thriller", "2025", "7.3", "108 min", cyan),
        iptvVod("vod-8", "Rook", MediaKind.Movie, "Crime", "2023", "7.7", "121 min", violet),
        iptvVod("vod-9", "Stone & Sky", MediaKind.Movie, "Documentary", "2024", "8.1", "88 min", blue),
        iptvVod("vod-10", "Last Call", MediaKind.Movie, "Drama", "2022", "7.2", "102 min", rust),
    )

    val iptvSeries = listOf(
        iptvVod("series-1", "Harbor Unit", MediaKind.Series, "Crime", "2025", "7.9", "3 seasons", blue),
        iptvVod("series-2", "Northbound", MediaKind.Series, "Drama", "2024", "8.0", "2 seasons", moss),
        iptvVod("series-3", "Small Hours", MediaKind.Series, "Comedy", "2023", "7.6", "4 seasons", gold),
        iptvVod("series-4", "Static", MediaKind.Series, "Science Fiction", "2025", "8.2", "1 season", violet),
        iptvVod("series-5", "The Exchange", MediaKind.Series, "Thriller", "2024", "7.7", "2 seasons", rust),
        iptvVod("series-6", "Field Notes", MediaKind.Series, "Documentary", "2022", "8.4", "5 seasons", cyan),
    )

    val householdState = HouseholdState(
        profiles = listOf(
            HouseholdProfile(HouseholdProfileId("living-room"), "Living Room", "LR"),
            HouseholdProfile(HouseholdProfileId("alex"), "Alex", "A"),
            HouseholdProfile(HouseholdProfileId("sam"), "Sam", "S"),
            HouseholdProfile(HouseholdProfileId("kids"), "Kids", "K", isKids = true),
        ),
        selectedProfileId = HouseholdProfileId("living-room"),
        profilePreferences = listOf("living-room", "alex", "sam", "kids").associate {
            HouseholdProfileId(it) to ProfilePreferences()
        },
    )

    val localSourceState = LocalSourceState(
        profiles = listOf(
            LocalSourceProfile(LocalSourceId("metadata"), "Metadata", LocalSourceKind.StremioAddon),
            LocalSourceProfile(LocalSourceId("streams"), "Streams", LocalSourceKind.StremioAddon),
            LocalSourceProfile(LocalSourceId("subtitles"), "Subtitles", LocalSourceKind.StremioAddon),
            LocalSourceProfile(LocalSourceId("living-tv"), "Living TV", LocalSourceKind.Xtream),
        ),
    )

    fun profilePalette(id: HouseholdProfileId): ArtworkPalette = when (id.value) {
        "living-room" -> blue
        "alex" -> rust
        "sam" -> violet
        "kids" -> gold
        else -> slate
    }

    val presentation = TvPresentationSnapshot(
        householdState = householdState,
        featured = stremioMovies.take(5),
        liveChannels = liveChannels,
        popularMovies = stremioMovies,
        popularSeries = stremioSeries,
        iptvMovies = iptvMovies,
        iptvSeries = iptvSeries,
        continueWatchingByProfile = householdState.profiles.associate { profile ->
            profile.id to continueWatching(profile.id)
        },
        profilePalettes = householdState.profiles.associate { profile ->
            profile.id to profilePalette(profile.id)
        },
        fallbackProfilePalette = slate,
        sourceState = localSourceState,
    )

    private fun watchProgress(item: MediaItem, percent: Int): WatchProgress = WatchProgress(
        content = OnDemandContentRef.Stremio(
            type = if (item.kind == MediaKind.Movie) "movie" else "series",
            id = item.id,
        ),
        positionMillis = percent * 1_000L,
        durationMillis = 100_000,
        updatedAtEpochMillis = 10_000L - percent,
    )

    private fun stremioMovie(
        id: String,
        title: String,
        year: String,
        rating: String,
        runtime: String,
        palette: ArtworkPalette,
        vararg genres: String,
    ) = MediaItem(
        id = id,
        title = title,
        description = "Static Stremio catalog metadata for the Android TV layout study.",
        kind = MediaKind.Movie,
        palette = palette,
        info = StremioInfo(Meta(
            id = id,
            type = "movie",
            name = title,
            description = "Static Stremio catalog metadata for the Android TV layout study.",
            releaseInfo = year,
            imdbRating = rating,
            runtime = runtime,
            genres = genres.toList(),
        )),
    )

    private fun stremioSeries(id: String, title: String, palette: ArtworkPalette) = MediaItem(
        id = id,
        title = title,
        description = "A static series record shaped like Stremio meta and video resources.",
        kind = MediaKind.Series,
        palette = palette,
        info = StremioInfo(Meta(
            id = id,
            type = "series",
            name = title,
            description = "A static series record shaped like Stremio meta and video resources.",
            releaseInfo = "2024–",
            imdbRating = "8.0",
            runtime = "52 min",
            genres = listOf("Drama", "Mystery"),
            videos = listOf(
                Video("$id:1:1", "First Signal", season = 1, episode = 1, overview = "A new signal changes the town overnight."),
                Video("$id:1:2", "Open Channel", season = 1, episode = 2, overview = "The group follows the transmission north."),
                Video("$id:1:3", "Dead Air", season = 1, episode = 3, overview = "Silence reveals what the signal was hiding."),
            ),
        )),
    )

    private fun liveChannel(
        number: String,
        name: String,
        group: String,
        palette: ArtworkPalette,
        programme: String,
        start: String,
        end: String,
    ): MediaItem {
        val channelId = ChannelId(number)
        val epgId = EpgChannelId("mock-channel-$number")
        val categoryId = CategoryId("mock-${group.lowercase().replace(' ', '-')}")
        val startInstant = mockInstant(start)
        val endInstant = mockInstant(end)
        val laterEnd = Instant.fromEpochMilliseconds(endInstant.toEpochMilliseconds() + 3_600_000)
        val latestEnd = Instant.fromEpochMilliseconds(laterEnd.toEpochMilliseconds() + 3_600_000)
        return MediaItem(
            id = "live-$number",
            title = name,
            description = programme,
            kind = MediaKind.Live,
            palette = palette,
            info = IptvLiveInfo(
                channel = IptvChannelMetadata(
                    id = channelId,
                    name = name,
                    source = IptvSourceKind.Xtream,
                    kind = ChannelKind.Live,
                    number = number.toDouble(),
                    categoryIds = listOf(categoryId),
                    epgChannelId = epgId,
                ),
                groupName = group,
                now = ProgrammeInfo(
                    start = startInstant,
                    end = endInstant,
                    title = programme,
                    description = "Current programme synopsis from XMLTV EPG data.",
                ),
                upcoming = listOf(
                    ProgrammeInfo(endInstant, laterEnd, "Following programme", description = "The next scheduled programme."),
                    ProgrammeInfo(laterEnd, latestEnd, "Later", description = "Later programme from the guide."),
                ),
            ),
        )
    }

    private fun iptvVod(
        id: String,
        title: String,
        kind: MediaKind,
        category: String,
        year: String,
        rating: String,
        duration: String,
        palette: ArtworkPalette,
    ): MediaItem {
        val description = "Static IPTV ${kind.name.lowercase()} information for the design route."
        val categoryId = CategoryId("mock-${category.lowercase().replace(' ', '-')}")
        val movie = if (kind == MediaKind.Movie) {
            IptvMovieMetadata(
                id = MovieId(id),
                name = title,
                source = IptvSourceKind.Xtream,
                categoryIds = listOf(categoryId),
                containerExtension = "mkv",
                year = year,
                plot = description,
                genre = category,
                rating = rating.toDouble(),
            )
        } else null
        val series = if (kind == MediaKind.Series) {
            IptvSeriesMetadata(
                id = SeriesId(id),
                name = title,
                categoryIds = listOf(categoryId),
                year = year,
                plot = description,
                genre = category,
                rating = rating.toDouble(),
            )
        } else null
        val episodes = if (series != null) {
            listOf(
                IptvEpisodeMetadata(
                    id = EpisodeId("$id-1-1"),
                    seriesId = series.id,
                    title = "Arrival",
                    source = IptvSourceKind.Xtream,
                    season = 1.0,
                    episode = 1.0,
                    containerExtension = "mkv",
                    plot = "The first episode in the provider response.",
                ),
                IptvEpisodeMetadata(
                    id = EpisodeId("$id-1-2"),
                    seriesId = series.id,
                    title = "Crossing",
                    source = IptvSourceKind.Xtream,
                    season = 1.0,
                    episode = 2.0,
                    containerExtension = "mkv",
                    plot = "The story moves beyond the city.",
                ),
            )
        } else {
            emptyList()
        }
        return MediaItem(
            id = id,
            title = title,
            description = description,
            kind = kind,
            palette = palette,
            info = IptvVodInfo(
                movie = movie,
                series = series,
                episodeMetadata = episodes,
                categoryName = category,
                durationLabel = duration,
            ),
        )
    }

    private fun mockInstant(clock: String): Instant = Instant.parse("2026-08-27T${clock}:00Z")
}
