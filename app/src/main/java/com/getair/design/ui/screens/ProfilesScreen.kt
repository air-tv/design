package com.getair.design.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.getair.design.model.Profile
import com.getair.design.model.StaticData
import com.getair.design.ui.components.ColorArtwork
import com.getair.design.ui.theme.AirBorderWidth
import com.getair.design.ui.theme.AirCardShape

@Composable
fun ProfilesScreen(
    sideNavigationFocusRequester: FocusRequester,
    contentEntryFocusRequester: FocusRequester,
    onContentFocused: (FocusRequester) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 58.dp)) {
        Text("Who's watching?", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 28.dp))
        Text(
            "Profiles keep watch progress, favorites, and content limits separate.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(top = 32.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            itemsIndexed(StaticData.profiles, key = { _, profile -> profile.name }) { index, profile ->
                val profileFocusRequester = if (index == 0) {
                    contentEntryFocusRequester
                } else {
                    remember(profile.name) { FocusRequester() }
                }
                ProfileCard(
                    profile,
                    Modifier
                        .focusRequester(profileFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) onContentFocused(profileFocusRequester)
                        }
                        .then(
                            if (index == 0) Modifier.focusProperties {
                                left = sideNavigationFocusRequester
                            } else Modifier
                        ),
                )
            }
            item { AddProfileCard() }
        }
    }
}

@Composable
private fun ProfileCard(profile: Profile, modifier: Modifier = Modifier) {
    Surface(
        onClick = {},
        modifier = modifier,
        shape = ClickableSurfaceDefaults.shape(AirCardShape),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(AirBorderWidth, MaterialTheme.colorScheme.onSurface),
                shape = AirCardShape,
            )
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(112.dp).clip(CircleShape)) {
                ColorArtwork(profile.palette, Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(profile.initials, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Text(profile.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 14.dp))
            if (profile.isKids) {
                Text("Kids", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun AddProfileCard() {
    Surface(
        onClick = {},
        shape = ClickableSurfaceDefaults.shape(AirCardShape),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(AirBorderWidth, MaterialTheme.colorScheme.onSurface),
                shape = AirCardShape,
            )
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("+", style = MaterialTheme.typography.displaySmall)
                }
            }
            Text("Add profile", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 14.dp))
        }
    }
}
