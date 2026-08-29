package com.getair.design.model

import androidx.compose.runtime.Immutable
import com.getair.core.household.HouseholdProfileId
import com.getair.core.household.HouseholdState
import com.getair.core.source.LocalSourceState

/**
 * One bounded, immutable input for the TV UI.
 *
 * The design harness builds this from deterministic fixtures. A production host can build the
 * same shape from real presenters without making a screen depend on a repository or transport.
 */
@Immutable
class TvPresentationSnapshot(
    householdState: HouseholdState,
    featured: List<MediaItem>,
    liveChannels: List<MediaItem>,
    popularMovies: List<MediaItem>,
    popularSeries: List<MediaItem>,
    iptvMovies: List<MediaItem>,
    iptvSeries: List<MediaItem>,
    continueWatchingByProfile: Map<HouseholdProfileId, List<MediaItem>>,
    profilePalettes: Map<HouseholdProfileId, ArtworkPalette>,
    fallbackProfilePalette: ArtworkPalette,
    sourceState: LocalSourceState,
) {
    val householdState = householdState
    val featured = featured.toList()
    val liveChannels = liveChannels.toList()
    val popularMovies = popularMovies.toList()
    val popularSeries = popularSeries.toList()
    val iptvMovies = iptvMovies.toList()
    val iptvSeries = iptvSeries.toList()
    val continueWatchingByProfile = continueWatchingByProfile
        .mapValues { (_, items) -> items.toList() }
        .toMap()
    val profilePalettes = profilePalettes.toMap()
    val fallbackProfilePalette = fallbackProfilePalette
    val sourceState = sourceState

    init {
        require(this.featured.isNotEmpty()) { "At least one featured item is required" }
        require(this.featured.size <= MAX_FEATURED_ITEMS) { "Featured items exceed the UI bound" }
        require(this.liveChannels.size <= MAX_LIVE_CHANNELS) { "Live channels exceed the UI bound" }
        require(this.popularMovies.size <= MAX_HOME_ROW_ITEMS) { "Popular movies exceed the UI bound" }
        require(this.popularSeries.size <= MAX_HOME_ROW_ITEMS) { "Popular series exceed the UI bound" }
        require(this.iptvMovies.size <= MAX_IPTV_GRID_ITEMS) { "IPTV movies exceed the UI bound" }
        require(this.iptvSeries.size <= MAX_IPTV_GRID_ITEMS) { "IPTV series exceed the UI bound" }
        require(this.householdState.profiles.size <= MAX_PROFILES) { "Profiles exceed the UI bound" }
        require(this.sourceState.profiles.size <= MAX_SOURCES) { "Sources exceed the UI bound" }
        require(this.continueWatchingByProfile.size <= MAX_PROFILES) {
            "Continue-watching profiles exceed the UI bound"
        }
        require(this.continueWatchingByProfile.values.all { it.size <= MAX_CONTINUE_WATCHING_ITEMS }) {
            "Continue-watching items exceed the UI bound"
        }

        val profileIds = this.householdState.profiles.mapTo(mutableSetOf()) { it.id }
        require(this.continueWatchingByProfile.keys.all(profileIds::contains)) {
            "Continue-watching data contains an unknown profile"
        }
        require(profileIds.all(this.profilePalettes::containsKey)) {
            "Every profile requires an artwork palette"
        }

        requireUniqueIds("featured", this.featured)
        requireUniqueIds("live channels", this.liveChannels)
        requireUniqueIds("popular movies", this.popularMovies)
        requireUniqueIds("popular series", this.popularSeries)
        requireUniqueIds("IPTV movies", this.iptvMovies)
        requireUniqueIds("IPTV series", this.iptvSeries)
    }

    fun continueWatching(profileId: HouseholdProfileId?): List<MediaItem> =
        profileId?.let(continueWatchingByProfile::get).orEmpty()

    private fun requireUniqueIds(section: String, items: List<MediaItem>) {
        require(items.mapTo(mutableSetOf()) { it.id }.size == items.size) {
            "$section contains duplicate media IDs"
        }
    }

    private companion object {
        const val MAX_FEATURED_ITEMS = 8
        const val MAX_HOME_ROW_ITEMS = 64
        const val MAX_CONTINUE_WATCHING_ITEMS = 64
        const val MAX_LIVE_CHANNELS = 256
        const val MAX_IPTV_GRID_ITEMS = 256
        const val MAX_PROFILES = 16
        const val MAX_SOURCES = 64
    }
}
