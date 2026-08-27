/*
 * Adapted from Android TV Samples: JetStreamCompose/FeaturedMoviesCarousel.kt.
 * Copyright 2023 Google LLC. Licensed under Apache-2.0.
 */
package com.getair.design.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Carousel
import androidx.tv.material3.CarouselState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.getair.design.model.MediaItem
import com.getair.design.ui.components.ColorArtwork
import com.getair.design.ui.components.MediaFacts
import com.getair.design.ui.components.mediaKicker
import com.getair.design.ui.focus.requestFocusSafely
import com.getair.design.ui.theme.AirButtonShape

@OptIn(ExperimentalTvMaterial3Api::class)
private val CarouselSaver = Saver<CarouselState, Int>(
    save = { it.activeItemIndex },
    restore = { CarouselState(it) },
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeaturedCarousel(
    items: List<MediaItem>,
    onItemSelected: (MediaItem) -> Unit,
    downFocusRequester: FocusRequester,
    leftFocusRequester: FocusRequester,
    selfFocusRequester: FocusRequester,
    onContentFocused: (FocusRequester) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberSaveable(saver = CarouselSaver) { CarouselState(0) }
    var focused by remember { mutableStateOf(false) }
    Carousel(
        itemCount = items.size,
        carouselState = state,
        carouselIndicator = {},
        modifier = modifier
            .onPreviewKeyEvent {
                if (it.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (it.key) {
                    Key.DirectionLeft -> {
                        leftFocusRequester.requestFocusSafely()
                    }
                    Key.DirectionDown -> {
                        downFocusRequester.requestFocusSafely()
                    }
                    else -> false
                }
            }
            .focusProperties {
                down = downFocusRequester
                left = leftFocusRequester
            }
            .onFocusChanged {
                focused = it.hasFocus
                if (it.hasFocus) onContentFocused(selfFocusRequester)
            },
    ) { index ->
        val item = items[index]
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            )
            ColorArtwork(
                palette = item.palette,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .aspectRatio(16f / 9f),
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            0f to MaterialTheme.colorScheme.surface,
                            0.34f to MaterialTheme.colorScheme.surface,
                            0.56f to MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                            0.78f to MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
                            1f to Color.Transparent,
                        )
                    )
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.58f to Color.Transparent,
                            0.86f to MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                            1f to MaterialTheme.colorScheme.surface,
                        )
                    )
            )
            CarouselCopy(
                item = item,
            focused = focused,
            downFocusRequester = downFocusRequester,
            leftFocusRequester = leftFocusRequester,
            onItemSelected = { onItemSelected(item) },
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.62f),
            )
        }
    }
}

@Composable
private fun CarouselCopy(
    item: MediaItem,
    focused: Boolean,
    downFocusRequester: FocusRequester,
    leftFocusRequester: FocusRequester,
    onItemSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(start = 104.dp, end = 24.dp, top = 30.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Text(
            text = mediaKicker(item),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            text = item.description,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
        MediaFacts(item, Modifier.padding(top = 14.dp))
        AnimatedVisibility(visible = focused) {
            Button(
                onClick = onItemSelected,
                modifier = Modifier
                    .focusProperties {
                        down = downFocusRequester
                        left = leftFocusRequester
                    }
                    .padding(top = 8.dp),
                shape = ButtonDefaults.shape(AirButtonShape),
                scale = ButtonDefaults.scale(scale = 1f),
                colors = ButtonDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                    focusedContentColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("View details")
            }
        }
    }
}
