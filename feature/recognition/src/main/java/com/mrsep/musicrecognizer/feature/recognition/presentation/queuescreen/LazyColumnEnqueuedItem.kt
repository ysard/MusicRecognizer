package com.mrsep.musicrecognizer.feature.recognition.presentation.queuescreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mrsep.musicrecognizer.feature.recognition.domain.model.EnqueuedRecognition
import com.mrsep.musicrecognizer.feature.recognition.domain.model.EnqueuedRecognitionWithStatus
import com.mrsep.musicrecognizer.feature.recognition.domain.model.RemoteRecognitionResult
import com.mrsep.musicrecognizer.feature.recognition.domain.model.ScheduledJobStatus
import com.mrsep.musicrecognizer.core.ui.R as UiR
import com.mrsep.musicrecognizer.core.strings.R as StringsR
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LazyColumnEnqueuedItem(
    enqueuedWithStatus: EnqueuedRecognitionWithStatus,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onDeleteEnqueued: (recognitionId: Int) -> Unit,
    onRenameEnqueued: (recognitionId: Int, name: String) -> Unit,
    onStartPlayRecord: (recognitionId: Int) -> Unit,
    onStopPlayRecord: () -> Unit,
    onEnqueueRecognition: (recognitionId: Int, forceLaunch: Boolean) -> Unit,
    onCancelRecognition: (recognitionId: Int) -> Unit,
    onNavigateToTrackScreen: (trackId: String) -> Unit,
    menuEnabled: Boolean,
    selected: Boolean,
    onClick: (recognitionId: Int) -> Unit,
    onLongClick: (recognitionId: Int) -> Unit
) {
    val enqueued = enqueuedWithStatus.enqueued
    val containerColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        label = "containerColor"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(
                color = containerColor,
                shape = MaterialTheme.shapes.large
            )
            .combinedClickable(
                onLongClick = { onLongClick(enqueued.id) },
                onClick = { onClick(enqueued.id) },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            shape = CircleShape,
            onClick = {
                if (isPlaying) {
                    onStopPlayRecord()
                } else {
                    onStartPlayRecord(enqueued.id)
                }
            },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(48.dp)
        ) {
            AnimatedContent(
                targetState = isPlaying,
                contentAlignment = Alignment.Center,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) +
                            scaleIn(
                                initialScale = 0f,
                                animationSpec = tween(220)
                            )).togetherWith(
                        fadeOut(animationSpec = tween(220)) +
                                scaleOut(
                                    targetScale = 0f,
                                    animationSpec = tween(220)
                                )
                    )
                }, label = "PlayerIconTransition"
            ) { playing ->
                if (playing) {
                    Icon(
                        painter = painterResource(UiR.drawable.baseline_pause_24),
                        contentDescription = stringResource(StringsR.string.stop_player),
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(StringsR.string.start_player),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = getTitle(enqueued),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = getStatusMessage(enqueuedWithStatus),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            val createdTime = enqueued.creationDate.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
            Text(
                text = stringResource(StringsR.string.format_created, createdTime),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        var menuExpanded by remember { mutableStateOf(false) }
        var renameDialogVisible by remember { mutableStateOf(false) }
        Box(contentAlignment = Alignment.Center) {
            this@Row.AnimatedVisibility(
                visible = menuEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = { menuExpanded = !menuExpanded }, enabled = menuEnabled) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(StringsR.string.show_more)
                    )
                }
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(StringsR.string.rename)) },
                    onClick = {
                        menuExpanded = false
                        renameDialogVisible = true
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = stringResource(StringsR.string.edit)
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(StringsR.string.delete)) },
                    onClick = {
                        menuExpanded = false
                        onDeleteEnqueued(enqueued.id)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = stringResource(StringsR.string.delete)
                        )
                    }
                )
                Divider()
                when (enqueuedWithStatus.status) {
                    ScheduledJobStatus.INACTIVE -> {
                        when (enqueued.result) {
                            is RemoteRecognitionResult.Error,
                            RemoteRecognitionResult.NoMatches,
                            null -> {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(StringsR.string.enqueue_recognition),
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onEnqueueRecognition(enqueued.id, false)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(UiR.drawable.baseline_schedule_send_24),
                                            contentDescription = stringResource(StringsR.string.enqueue_recognition),
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(StringsR.string.force_recognition_launch),
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onEnqueueRecognition(enqueued.id, true)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(UiR.drawable.baseline_send_24),
                                            contentDescription = stringResource(StringsR.string.force_recognition_launch),
                                        )
                                    }
                                )
                            }

                            is RemoteRecognitionResult.Success -> {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(StringsR.string.show_track),
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onNavigateToTrackScreen(enqueued.result.track.id)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(UiR.drawable.baseline_audio_file_24),
                                            contentDescription = stringResource(StringsR.string.show_track)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    ScheduledJobStatus.ENQUEUED,
                    ScheduledJobStatus.RUNNING -> {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(StringsR.string.cancel_recognition),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onCancelRecognition(enqueued.id)
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(UiR.drawable.baseline_cancel_schedule_send_24),
                                    contentDescription = stringResource(StringsR.string.cancel_recognition)
                                )
                            }
                        )
                    }
                }
            }
        }
        if (renameDialogVisible) {
            val title = getTitle(enqueued)
            RenameDialog(
                initialName = title,
                onConfirmClick = { newName ->
                    onRenameEnqueued(enqueued.id, newName)
                    renameDialogVisible = false
                },
                onDismissClick = {
                    renameDialogVisible = false
                }
            )
        }
    }

}

@Composable
private fun RenameDialog(
    initialName: String,
    modifier: Modifier = Modifier,
    onConfirmClick: (String) -> Unit,
    onDismissClick: () -> Unit
) {
    var newName by rememberSaveable { mutableStateOf(initialName) }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissClick,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmClick(newName)
                },
                enabled = newName.isNotBlank()
            ) {
                Text(text = stringResource(StringsR.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissClick) {
                Text(text = stringResource(StringsR.string.cancel))
            }
        },
        title = {
            Text(
                text = stringResource(StringsR.string.rename),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                trailingIcon = {
                    Crossfade(
                        targetState = newName.isNotEmpty(),
                        label = ""
                    ) { visible ->
                        if (visible) {
                            IconButton(onClick = { newName = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(StringsR.string.clear)
                                )
                            }
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.small
            )
        },
    )
}

@Composable
private fun getTitle(enqueued: EnqueuedRecognition) =
    enqueued.title.ifBlank { stringResource(StringsR.string.untitled_recognition) }

@Composable
private fun getStatusMessage(enqueuedWithStatus: EnqueuedRecognitionWithStatus): String {
    val statusDescription = when (enqueuedWithStatus.status) {
        ScheduledJobStatus.INACTIVE -> {
            when (enqueuedWithStatus.enqueued.result) {
                RemoteRecognitionResult.Error.BadConnection -> stringResource(StringsR.string.bad_internet_connection)
                is RemoteRecognitionResult.Error.BadRecording -> stringResource(StringsR.string.recording_error)
                is RemoteRecognitionResult.Error.HttpError -> stringResource(StringsR.string.bad_network_response)
                is RemoteRecognitionResult.Error.UnhandledError -> stringResource(StringsR.string.internal_error)
                is RemoteRecognitionResult.Error.WrongToken -> if (enqueuedWithStatus.enqueued.result.isLimitReached)
                    stringResource(StringsR.string.token_limit_reached)
                else
                    stringResource(StringsR.string.wrong_token)

                RemoteRecognitionResult.NoMatches -> stringResource(StringsR.string.no_matches_found)
                is RemoteRecognitionResult.Success -> stringResource(StringsR.string.track_found)
                null -> stringResource(StringsR.string.idle)
            }
        }

        ScheduledJobStatus.ENQUEUED -> stringResource(StringsR.string.enqueued)
        ScheduledJobStatus.RUNNING -> stringResource(StringsR.string.running)
    }
    return stringResource(StringsR.string.format_status, statusDescription)
}