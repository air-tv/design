/*
 * Home structure adapted from Android TV Samples: JetStreamCompose/HomeScreen.kt.
 * Copyright 2023 Google LLC. Licensed under Apache-2.0.
 */
package com.getair.design.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import com.getair.design.model.MediaItem
import com.getair.design.model.StaticData
import com.getair.design.ui.components.MediaRow
import com.getair.design.ui.components.RowCardStyle

@Composable
fun HomeScreen(
    onItemSelected: (MediaItem) -> Unit,
    sideNavigationFocusRequester: FocusRequester,
    contentEntryFocusRequester: FocusRequester,
    onContentFocused: (FocusRequester) -> Unit,
) {
    val continueWatchingFocusRequester = remember { FocusRequester() }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .focusProperties {
                left = sideNavigationFocusRequester
                down = continueWatchingFocusRequester
            },
        contentPadding = PaddingValues(bottom = 108.dp),
    ) {
        item(contentType = "FeaturedCarousel") {
            FeaturedCarousel(
                items = StaticData.stremioMovies.take(5),
                onItemSelected = onItemSelected,
                downFocusRequester = continueWatchingFocusRequester,
                leftFocusRequester = sideNavigationFocusRequester,
                selfFocusRequester = contentEntryFocusRequester,
                onContentFocused = onContentFocused,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .focusRequester(contentEntryFocusRequester),
            )
        }
        item(contentType = "ContinueWatching") {
            MediaRow(
                title = "Continue watching",
                items = StaticData.continueWatching,
                cardStyle = RowCardStyle.Landscape,
                onItemSelected = onItemSelected,
                firstItemFocusRequester = continueWatchingFocusRequester,
                firstItemLeftFocusRequester = sideNavigationFocusRequester,
                onContentFocused = onContentFocused,
                modifier = Modifier.padding(top = 18.dp),
            )
        }
        item(contentType = "LiveTv") {
            MediaRow(
                title = "Live TV — On now",
                items = StaticData.liveChannels,
                cardStyle = RowCardStyle.Landscape,
                onItemSelected = onItemSelected,
                firstItemLeftFocusRequester = sideNavigationFocusRequester,
                onContentFocused = onContentFocused,
                modifier = Modifier.padding(top = 32.dp),
            )
        }
        item(contentType = "PopularMovies") {
            MediaRow(
                title = "Popular movies",
                items = StaticData.stremioMovies,
                cardStyle = RowCardStyle.Poster,
                onItemSelected = onItemSelected,
                firstItemLeftFocusRequester = sideNavigationFocusRequester,
                onContentFocused = onContentFocused,
                modifier = Modifier.padding(top = 32.dp),
            )
        }
        item(contentType = "PopularSeries") {
            MediaRow(
                title = "Popular series",
                items = StaticData.stremioSeries,
                cardStyle = RowCardStyle.Poster,
                onItemSelected = onItemSelected,
                firstItemLeftFocusRequester = sideNavigationFocusRequester,
                onContentFocused = onContentFocused,
                modifier = Modifier.padding(top = 32.dp),
            )
        }
    }
}
