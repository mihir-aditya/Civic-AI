package com.nagarrakshak.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nagarrakshak.data.AuthManager
import com.nagarrakshak.ui.theme.PrimaryColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        // Run animations concurrently
        alphaAnim.animateTo(1f, animationSpec = tween(1200))
        scaleAnim.animateTo(1f, animationSpec = tween(1200))
        
        delay(1000) // Keep visible for an extra second
        
        if (authManager.isLoggedIn || authManager.isGuest) {
            onNavigateToHome()
        } else {
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate 900
                        Color(0xFF1E293B)  // Slate 800
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Shield Logo Checkmark with pulse animation effect
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = 54.sp)
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            Text(
                text = "NAGAR RAKSHAK",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier
                    .alpha(alphaAnim.value)
                    .scale(scaleAnim.value),
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Civic Safety & Hazard Portal",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF94A3B8), // Slate 400
                modifier = Modifier.alpha(alphaAnim.value)
            )
        }
    }
}
