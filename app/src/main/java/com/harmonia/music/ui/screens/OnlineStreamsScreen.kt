package com.harmonia.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.Track
import com.harmonia.music.ui.components.StreamItem
import com.harmonia.music.ui.theme.AccentCyan
import com.harmonia.music.ui.theme.DarkBackground
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary

@Composable
fun OnlineStreamsScreen(
    streams: List<Track>,
    playbackState: PlaybackState,
    selectedGenre: String?,
    onGenreSelect: (String?) -> Unit,
    onStreamClick: (Track) -> Unit,
    onFavoriteToggle: (Track) -> Unit,
    onAddCustomStream: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val genres = remember {
        listOf(
            "Ambient / Chill",
            "Synthwave / Cyberpunk",
            "Deep Space Ambient",
            "Lounge / Retro",
            "Indie Rock",
            "World / Goa"
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Genre Chips Horizontal Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedFilterChip(
                selected = selectedGenre == null,
                onClick = { onGenreSelect(null) },
                label = { Text("All Streams") },
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = AccentCyan,
                    selectedLabelColor = DarkBackground,
                    containerColor = DarkSurfaceElevated,
                    labelColor = TextSecondary
                )
            )

            genres.forEach { genre ->
                ElevatedFilterChip(
                    selected = selectedGenre == genre,
                    onClick = { onGenreSelect(genre) },
                    label = { Text(genre) },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = AccentCyan,
                        selectedLabelColor = DarkBackground,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // Stream Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Add Stream button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Curated Broadcasts (${streams.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceElevated,
                            contentColor = AccentCyan
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Custom Stream",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Stream",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            items(streams, key = { it.id }) { stream ->
                StreamItem(
                    stream = stream,
                    isCurrentTrack = playbackState.currentTrack?.id == stream.id,
                    isPlaying = playbackState.isPlaying,
                    onStreamClick = { onStreamClick(stream) },
                    onFavoriteToggle = { onFavoriteToggle(stream) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddCustomStreamDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, url, genre ->
                onAddCustomStream(name, url, genre)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddCustomStreamDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, url: String, genre: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceVariant,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Custom Stream", color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Station / Stream Name") },
                    placeholder = { Text("e.g. My Favorite Radio") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        focusedLabelColor = AccentCyan,
                        cursorColor = AccentCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Stream URL") },
                    placeholder = { Text("https://example.com/stream.mp3") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        focusedLabelColor = AccentCyan,
                        cursorColor = AccentCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre / Category") },
                    placeholder = { Text("e.g. Electronic, Jazz, News") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        focusedLabelColor = AccentCyan,
                        cursorColor = AccentCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || url.isBlank()) {
                        errorMessage = "Station name and Stream URL are required"
                    } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        errorMessage = "URL must start with http:// or https://"
                    } else {
                        onConfirm(name.trim(), url.trim(), genre.trim().ifBlank { "Custom Radio" })
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor = DarkBackground
                )
            ) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
