package com.mrsep.musicrecognizer.feature.track.presentation.track

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mrsep.musicrecognizer.feature.track.domain.model.MusicService
import com.mrsep.musicrecognizer.feature.track.domain.model.TrackLink
import kotlinx.collections.immutable.ImmutableList
import com.mrsep.musicrecognizer.core.strings.R as StringsR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun ShareBottomSheet(
    title: String,
    artist: String,
    album: String?,
    year: String?,
    lyrics: String?,
    trackLinks: ImmutableList<TrackLink>,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onShareClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        windowInsets = WindowInsets.navigationBars,
        modifier = modifier
    ) {
        var titleSelected by rememberSaveable(title) { mutableStateOf(true) }
        var artistSelected by rememberSaveable(artist) { mutableStateOf(true) }
        var albumSelected by rememberSaveable(album) { mutableStateOf(false) }
        var yearSelected by rememberSaveable(year) { mutableStateOf(false) }
        var selectedMusicServices by rememberSaveable(trackLinks) {
            mutableStateOf(setOf<MusicService>())
        }
        var lyricsSelected by rememberSaveable(year) { mutableStateOf(false) }

        fun buildStringToShare() = buildString {
            if (titleSelected) append(title)
            if (artistSelected) if (isNotBlank()) append(" - $artist") else append(artist)
            if (albumSelected) if (isNotBlank()) append(" - $album") else append(album)
            if (yearSelected) if (isNotBlank()) append(" ($year)") else append(year)
            if (lyricsSelected) if (isNotEmpty()) append("\n\n$lyrics") else append(lyrics)
            trackLinks
                .filter { selectedMusicServices.contains(it.service) }
                .joinToString("\n") { it.url }
                .takeIf { serviceUrls -> serviceUrls.isNotBlank() }?.let { serviceUrls ->
                    if (isNotEmpty()) append("\n\n$serviceUrls") else append(serviceUrls)
                }
        }
        val shareAllowed = titleSelected || artistSelected || albumSelected ||
                yearSelected || selectedMusicServices.isNotEmpty() || lyricsSelected

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                text = stringResource(StringsR.string.share_options),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            ShareGroup(
                title = stringResource(StringsR.string.metadata),
                modifier = Modifier.padding(top = 24.dp)
            ) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FilterChip(
                        selected = titleSelected,
                        onClick = { titleSelected = !titleSelected },
                        label = { Text(text = stringResource(StringsR.string.title)) }
                    )
                    FilterChip(
                        selected = artistSelected,
                        onClick = { artistSelected = !artistSelected },
                        label = { Text(text = stringResource(StringsR.string.artist)) }
                    )
                    album?.let {
                        FilterChip(
                            selected = albumSelected,
                            onClick = { albumSelected = !albumSelected },
                            label = { Text(text = stringResource(StringsR.string.album)) }
                        )
                    }
                    year?.let {
                        FilterChip(
                            selected = yearSelected,
                            onClick = { yearSelected = !yearSelected },
                            label = { Text(text = stringResource(StringsR.string.year)) }
                        )
                    }
                    lyrics?.let {
                        FilterChip(
                            selected = lyricsSelected,
                            onClick = { lyricsSelected = !lyricsSelected },
                            label = { Text(text = stringResource(StringsR.string.lyrics)) }
                        )
                    }
                }
            }
            ShareGroup(
                title = stringResource(StringsR.string.music_services_links),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    trackLinks.forEach { (_, serviceType) ->
                        val selected = selectedMusicServices.contains(serviceType)
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedMusicServices = if (selected) {
                                    selectedMusicServices - serviceType
                                } else {
                                    selectedMusicServices + serviceType
                                }
                            },
                            label = { Text(text = stringResource(serviceType.titleId())) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { onCopyClick(buildStringToShare()) },
                    enabled = shareAllowed
                ) {
                    Text(
                        text = stringResource(StringsR.string.copy),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                TextButton(
                    onClick = { onShareClick(buildStringToShare()) },
                    enabled = shareAllowed
                ) {
                    Text(
                        text = stringResource(StringsR.string.share),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ShareGroup(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Divider(modifier = Modifier.fillMaxWidth())
        Text(
            text = title,
            modifier = Modifier.padding(16.dp)
        )
        content()
    }
}