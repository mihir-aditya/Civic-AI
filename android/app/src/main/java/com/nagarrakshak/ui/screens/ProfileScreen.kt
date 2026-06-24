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
import androidx.compose.ui.unit.sp
import com.nagarrakshak.ui.theme.PrimaryColor
import com.nagarrakshak.ui.theme.SafeColor

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.nagarrakshak.data.AuthManager

@Composable
fun ProfileScreen(
    onNavigateToDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    
    val name = authManager.userName ?: "Ravi Kumar"
    val email = authManager.userEmail ?: "ravi.kumar@nagarrakshak.org"
    val avatar = authManager.userPhotoUrl ?: "👤"
    val loginTypeDesc = when(authManager.loginType) {
        "google" -> "Signed in with Google"
        "email" -> "Signed in with Email"
        "guest" -> "Guest Mode"
        else -> "Registered Citizen"
    }

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
                        Text(avatar, style = MaterialTheme.typography.titleLarge, fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = loginTypeDesc, style = MaterialTheme.typography.bodyMedium, color = PrimaryColor, fontWeight = FontWeight.SemiBold)
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

        item {
            Button(
                onClick = {
                    authManager.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Sign Out", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
            }
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
