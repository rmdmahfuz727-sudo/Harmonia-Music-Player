package com.harmonia.music.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.harmonia.music.ui.theme.MochyCoral

/**
 * Mochy Signature Floating Frosted Capsule Navigation Bar (5 Tabs).
 * Floating frosted pill dock:
 *  0: Home (Discover & stream online)
 *  1: Explore (Search & Moods)
 *  2: Library (Songs, Albums, Artists)
 *  3: Playlists (Custom queues & favorites)
 *  4: Profile (Mahfuz Rahman ID & Settings)
 */
@Composable
fun MochyBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val vfx = LocalSakugaVfx.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Frosted Capsule Dock
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color.Black.copy(alpha = 0.65f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF16161B).copy(alpha = 0.94f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.96f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MochyNavTabItem(
                    index = 0,
                    isSelected = selectedTab == 0,
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    contentDescription = "Home",
                    onClick = {
                        vfx.triggerMangaSfx("カチッ!", Offset(120f, 1900f), color = MochyCoral)
                        onTabSelected(0)
                    }
                )

                MochyNavTabItem(
                    index = 1,
                    isSelected = selectedTab == 1,
                    selectedIcon = Icons.Filled.Search,
                    unselectedIcon = Icons.Outlined.Search,
                    contentDescription = "Explore",
                    onClick = {
                        vfx.triggerMangaSfx("カチッ!", Offset(300f, 1900f), color = MochyCoral)
                        onTabSelected(1)
                    }
                )

                MochyNavTabItem(
                    index = 2,
                    isSelected = selectedTab == 2,
                    selectedIcon = Icons.Filled.LibraryMusic,
                    unselectedIcon = Icons.Outlined.LibraryMusic,
                    contentDescription = "Library",
                    onClick = {
                        vfx.triggerMangaSfx("カチッ!", Offset(500f, 1900f), color = MochyCoral)
                        onTabSelected(2)
                    }
                )

                MochyNavTabItem(
                    index = 3,
                    isSelected = selectedTab == 3,
                    selectedIcon = Icons.Filled.QueueMusic,
                    unselectedIcon = Icons.Outlined.QueueMusic,
                    contentDescription = "Playlists",
                    onClick = {
                        vfx.triggerMangaSfx("カチッ!", Offset(700f, 1900f), color = MochyCoral)
                        onTabSelected(3)
                    }
                )

                MochyNavTabItem(
                    index = 4,
                    isSelected = selectedTab == 4,
                    selectedIcon = Icons.Filled.PlayArrow,
                    unselectedIcon = Icons.Filled.PlayArrow,
                    contentDescription = "Profile",
                    isCustomMochyIcon = true,
                    onClick = {
                        vfx.triggerMangaSfx("カチッ!", Offset(900f, 1900f), color = MochyCoral)
                        onTabSelected(4)
                    }
                )
            }
        }
    }
}

@Composable
private fun MochyNavTabItem(
    index: Int,
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    contentDescription: String,
    isCustomMochyIcon: Boolean = false,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nav_scale_$index"
    )

    val activeBgColor by animateColorAsState(
        targetValue = if (isSelected) Color.White.copy(alpha = 0.16f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_bg_$index"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF7E7E88),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_icon_$index"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(activeBgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isCustomMochyIcon) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (isSelected) MochyCoral else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = contentDescription,
                    tint = if (isSelected) Color.White else iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
