/*
 * Card structure adapted from Android TV Samples: JetStreamCompose/MovieCard.kt.
 * Copyright 2023 Google LLC. Licensed under Apache-2.0.
 */
package com.getair.design.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.getair.design.model.MediaItem
import com.getair.design.model.IptvLiveInfo
import com.getair.design.ui.theme.AirBorderWidth
import com.getair.design.ui.theme.AirCardShape

@Composable
fun PosterMediaCard(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StandardCardContainer(
        modifier = Modifier.width(156.dp),
        title = {
            Text(
                text = item.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        },
        imageCard = {
            FocusSurface(onClick = onClick, modifier = modifier) {
                ColorArtwork(
                    palette = item.palette,
                    modifier = Modifier.fillMaxWidth().height(224.dp),
                )
            }
        },
    )
}

@Composable
fun LandscapeMediaCard(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(Modifier.width(185.dp)) {
        FocusSurface(onClick = onClick, modifier = modifier) {
            Box {
                ColorArtwork(
                    palette = item.palette,
                    modifier = Modifier.fillMaxWidth().height(104.dp),
                ) {
                    val liveInfo = item.info as? IptvLiveInfo
                    if (liveInfo != null) {
                        Box(
                            Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .height(70.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.78f),
                                        ),
                                    )
                                )
                        )
                        Column(
                            Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                "${liveInfo.channelNumber}  ${item.title}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                "${liveInfo.now.start}–${liveInfo.now.end}  ${liveInfo.now.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.78f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
                item.progress?.let { progress ->
                    Box(
                        Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .height(5.dp)
                            .background(Color.Black.copy(alpha = 0.45f))
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .height(5.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(
            text = item.description,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun FocusSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = ClickableSurfaceDefaults.shape(AirCardShape),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(AirBorderWidth, MaterialTheme.colorScheme.onSurface),
                shape = AirCardShape,
            )
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        content = content,
    )
}
