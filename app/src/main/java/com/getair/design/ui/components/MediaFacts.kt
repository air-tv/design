package com.getair.design.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.getair.design.model.IptvLiveInfo
import com.getair.design.model.IptvVodInfo
import com.getair.design.model.MediaItem
import com.getair.design.model.StremioInfo

private data class MediaFact(val value: String, val label: String)

fun mediaKicker(item: MediaItem): String = when (val info = item.info) {
    is StremioInfo -> "Stremio  |  ${info.type.replaceFirstChar { it.uppercase() }}"
    is IptvLiveInfo -> "IPTV  |  Live channel"
    is IptvVodInfo -> "IPTV  |  ${info.type.name}"
}

@Composable
fun MediaFacts(item: MediaItem, modifier: Modifier = Modifier) {
    val facts = when (val info = item.info) {
        is StremioInfo -> listOf(
            MediaFact(info.releaseInfo, "Year"),
            MediaFact(info.imdbRating, "Rating"),
            MediaFact(info.runtime, "Runtime"),
        )
        is IptvLiveInfo -> listOf(
            MediaFact(info.channelNumber, "Channel"),
            MediaFact(info.group, "Group"),
            MediaFact(info.streamFormat, "Stream"),
        )
        is IptvVodInfo -> listOf(
            MediaFact(info.year, "Year"),
            MediaFact(info.rating, "Rating"),
            MediaFact(info.duration, "Duration"),
        )
    }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(34.dp)) {
        facts.forEach { fact ->
            Column {
                Text(fact.value, style = MaterialTheme.typography.titleMedium)
                Text(
                    fact.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
    }
}
