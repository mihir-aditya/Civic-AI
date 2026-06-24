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
fun ProfileScreen(onNavigateToDetail: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(PrimaryColor.copy(alpha = 0.1f), shape = RoundedCornerShape(40.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Ravi Kumar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Civic Champion • Level 4", style = MaterialTheme.typography.bodyMedium, color = PrimaryColor)
                }
            }
        }

        // Statistics grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(value = "24", label = "Reports", modifier = Modifier.weight(1f))
                StatCard(value = "156", label = "Verifications", modifier = Modifier.weight(1f))
                StatCard(value = "8", label = "Wards Covered", modifier = Modifier.weight(1f))
            }
        }

        item {
            Text("Recent Contributions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            RecentContributionItem(
                title = "Open Drain reported",
                date = "24 Jun 2026",
                points = "+150 pts",
                onClick = { onNavigateToDetail("2") }
            )
        }

        item {
            RecentContributionItem(
                title = "Verified Pothole on Road",
                date = "23 Jun 2026",
                points = "+50 pts",
                onClick = { onNavigateToDetail("1") }
            )
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryColor)
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
fun RecentContributionItem(
    title: String,
    date: String,
    points: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = date, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Text(text = points, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = SafeColor)
        }
    }
}
