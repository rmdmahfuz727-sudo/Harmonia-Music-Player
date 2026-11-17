package com.harmonia.music.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.Track
import com.harmonia.music.ui.components.StreamItem
import com.harmonia.music.ui.components.sakugaSquash
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyPillBackground
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
        // Mochy Genre Chips Horizontal Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All Streams" Pill
            val isAllSelected = selectedGenre == null
            Surface(
                color = if (isAllSelected) MochyCoral else MochyPillBackground,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .sakugaSquash(squashAmount = 0.88f) { onGenreSelect(null) }
                    .then(
                        if (!isAllSelected) Modifier.border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                        else Modifier
                    )
            ) {
                Text(
                    text = "All Streams",
                    color = if (isAllSelected) Color.White else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }

            genres.forEach { genre ->
                val isSelected = selectedGenre == genre
                Surface(
                    color = if (isSelected) MochyCoral else MochyPillBackground,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .sakugaSquash(squashAmount = 0.88f) { onGenreSelect(genre) }
                        .then(
                            if (!isSelected) Modifier.border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                            else Modifier
                        )
                ) {
                    Text(
                        text = genre,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Stream Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Mochy Add Stream button
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
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )

                    Surface(
                        color = MochyPillBackground,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .sakugaSquash(squashAmount = 0.88f) { showAddDialog = true }
                            .border(1.dp, MochyCoral.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Custom Stream",
                                tint = MochyCoral,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add Stream",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
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
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = null,
                    tint = MochyCoral,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Custom Stream", color = TextPrimary, fontWeight = FontWeight.Bold)
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
                        focusedBorderColor = MochyCoral,
                        focusedLabelColor = MochyCoral,
                        cursorColor = MochyCoral
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Stream URL") },
                    placeholder = { Text("https://example.com/stream.mp3") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MochyCoral,
                        focusedLabelColor = MochyCoral,
                        cursorColor = MochyCoral
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre / Category") },
                    placeholder = { Text("e.g. Electronic, Jazz, News") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MochyCoral,
                        focusedLabelColor = MochyCoral,
                        cursorColor = MochyCoral
                    ),
                    shape = RoundedCornerShape(14.dp),
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
                    containerColor = MochyCoral,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.sakugaSquash(squashAmount = 0.9f)
            ) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
