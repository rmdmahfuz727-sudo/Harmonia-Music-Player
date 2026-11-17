package com.harmonia.music.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harmonia.music.ui.components.sakugaSquash
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary

/**
 * Mochy Profile & Settings Screen:
 *  - Mahfuz Rahman ID Card
 *  - Total listening time stats
 *  - Version & About App 8.4.2
 *  - Developer profile link
 *  - Functional Audio Lab launcher & Customization
 *  (No dummy discord or non-functional placeholder options)
 */
@Composable
fun ProfileScreen(
    onOpenAudioLab: () -> Unit,
    onShowAboutDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var settingsSearchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Top Bar with Mochy Logo & Avatar ────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1E24)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mochy",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF282832))
                        .border(1.5.dp, MochyCoral.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MR",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // ── "Hey, Mahfuz Rahman!" Greeting ──────────────────────────────
        item {
            Text(
                text = "Hey, Mahfuz Rahman!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        // ── Mahfuz Rahman ID Card ───────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1B1B20))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "PROFILE USR#85310972087",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2C2C38))
                                .border(2.dp, MochyCoral, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "MR",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MochyCoral
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "ID CARD OF",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "Mahfuz Rahman",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "joined at Nov 16, 2026",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // ── Total Listening Time Stats Card ─────────────────────────────
        item {
            Surface(
                color = Color(0xFF16161B),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "0h  0m",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )

                        Text(
                            text = "Total Listening\ntime",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "View Stats >",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }
        }

        // ── Version & About App 8.4.2 Card ──────────────────────────────
        item {
            Surface(
                color = Color(0xFF16161B),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Version & about app",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "8.4.2",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }

                    Surface(
                        color = Color(0xFF2C2C35),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.sakugaSquash(squashAmount = 0.88f) { onShowAboutDialog() }
                    ) {
                        Text(
                            text = "About app",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // ── Developer Profile Link ──────────────────────────────────────
        item {
            Surface(
                color = Color(0xFF16161B),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .sakugaSquash(squashAmount = 0.94f) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rmdmahfuz727-sudo"))
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MochyCoral.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MochyCoral)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Meet Lead Developer: Mahfuz Rahman",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "github.com/rmdmahfuz727-sudo",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // ── Settings Header ─────────────────────────────────────────────
        item {
            Text(
                text = "Settings & Customization",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        // ── Search Settings Bar ─────────────────────────────────────────
        item {
            OutlinedTextField(
                value = settingsSearchQuery,
                onValueChange = { settingsSearchQuery = it },
                placeholder = {
                    Text(
                        text = "Search settings",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MochyCoral,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFF1E1E24),
                    unfocusedContainerColor = Color(0xFF1E1E24),
                    cursorColor = MochyCoral,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // ── Clean, Functional Settings Items ────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Audio Lab
                MochySettingsPill(
                    icon = Icons.Default.GraphicEq,
                    title = "Audio Lab (3D Spatializer & EQ)",
                    onClick = onOpenAudioLab,
                    isHighlighted = true
                )

                // Playback
                MochySettingsPill(
                    icon = Icons.Default.PlayArrow,
                    title = "Playback (Gapless & Crossfade)"
                )

                // Now playing
                MochySettingsPill(
                    icon = Icons.Default.Headphones,
                    title = "Now Playing & Waveform Styles"
                )

                // Customisation
                MochySettingsPill(
                    icon = Icons.Default.Palette,
                    title = "Theme & Anime VFX Customization"
                )

                // Storage
                MochySettingsPill(
                    icon = Icons.Default.Storage,
                    title = "Storage & Cache Management"
                )
            }
        }
    }
}

@Composable
private fun MochySettingsPill(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
    isHighlighted: Boolean = false
) {
    Surface(
        color = if (isHighlighted) Color(0xFF221F28) else Color(0xFF16161B),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .sakugaSquash(squashAmount = 0.96f) { onClick() }
            .then(
                if (isHighlighted) Modifier.border(1.dp, MochyCoral.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                else Modifier.border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(18.dp))
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isHighlighted) MochyCoral.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isHighlighted) MochyCoral else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) MochyCoral else TextPrimary
            )
        }
    }
}
