package com.getair.design.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.getair.design.model.MediaItem

enum class RowCardStyle { Poster, Landscape }

@Composable
fun MediaRow(
    title: String,
    items: List<MediaItem>,
    cardStyle: RowCardStyle,
    onItemSelected: (MediaItem) -> Unit,
    firstItemFocusRequester: FocusRequester? = null,
    firstItemLeftFocusRequester: FocusRequester? = null,
    onContentFocused: (FocusRequester) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 104.dp, bottom = 16.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(start = 104.dp, end = 58.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                val cardFocusRequester = if (index == 0 && firstItemFocusRequester != null) {
                    firstItemFocusRequester
                } else {
                    remember(item.id) { FocusRequester() }
                }
                val cardModifier = Modifier
                    .focusRequester(cardFocusRequester)
                    .onFocusChanged { if (it.isFocused) onContentFocused(cardFocusRequester) }
                    .then(
                        if (index == 0 && firstItemLeftFocusRequester != null) {
                            Modifier.focusProperties { left = firstItemLeftFocusRequester }
                        } else Modifier
                    )
                when (cardStyle) {
                    RowCardStyle.Poster -> PosterMediaCard(item, { onItemSelected(item) }, cardModifier)
                    RowCardStyle.Landscape -> LandscapeMediaCard(item, { onItemSelected(item) }, cardModifier)
                }
            }
        }
    }
}
