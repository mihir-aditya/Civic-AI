package com.nagarrakshak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nagarrakshak.ui.theme.PrimaryColor
import com.nagarrakshak.ui.theme.SafeColor

@Composable
fun LeaderboardScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Community Leaderboard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Top citizens helping Kota remain hazard-free",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        // Leaderboard Stats Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Your Rank", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                        Text("#14", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Reputation Points", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                        Text("1,250 pts", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Badge Showcase
        item {
            Text("Your Earned Badges", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BadgeChip("Safety Reporter", SafeColor)
                BadgeChip("Community Guardian", PrimaryColor)
                BadgeChip("Civic Champion", PrimaryColor)
            }
        }

        item {
            Text("Top Active Contributors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        // Top list
        item { LeaderboardItem(rank = 1, name = "Aarav Sharma", score = "4,820 pts", badge = "Community Hero") }
        item { LeaderboardItem(rank = 2, name = "Priya Patel", score = "3,950 pts", badge = "Community Hero") }
        item { LeaderboardItem(rank = 3, name = "Rohan Verma", score = "3,210 pts", badge = "Civic Champion") }
        item { LeaderboardItem(rank = 4, name = "Karan Singh", score = "2,980 pts", badge = "Civic Champion") }
    }
}

@Composable
fun BadgeChip(label: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LeaderboardItem(
    rank: Int,
    name: String,
    score: String,
    badge: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) PrimaryColor else Color.Gray,
                    modifier = Modifier.width(36.dp)
                )
                Column {
                    Text(text = name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(text = badge, style = MaterialTheme.typography.bodyMedium, color = PrimaryColor)
                }
            }

            Text(text = score, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
