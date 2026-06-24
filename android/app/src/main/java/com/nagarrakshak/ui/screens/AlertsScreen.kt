package com.nagarrakshak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.nagarrakshak.ui.theme.DangerColor
import com.nagarrakshak.ui.theme.WarningColor

@Composable
fun AlertsScreen(onNavigateToDetail: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Alerts & Notifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            AlertCard(
                title = "⚠ Open Drain 150m Ahead",
                category = "Emergency Alert",
                description = "An uncovered deep drain lies directly on the pavement path. Drive/walk with caution.",
                color = DangerColor,
                onClick = { onNavigateToDetail("2") }
            )
        }

        item {
            AlertCard(
                title = "⚠ Waterlogging Nearby",
                category = "Critical Hazard",
                description = "Talwandi intersection has accumulated 2 feet of rain water. Traffic heavily slowed down.",
                color = WarningColor,
                onClick = { onNavigateToDetail("1") }
            )
        }

        item {
            AlertCard(
                title = "⚠ High Risk Road Ahead",
                category = "Route Alert",
                description = "Sector 7 main street reports multiple potholes and surface failure. Avoid speed.",
                color = WarningColor,
                onClick = { onNavigateToDetail("1") }
            )
        }
    }
}

@Composable
fun AlertCard(
    title: String,
    category: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, shape = RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
