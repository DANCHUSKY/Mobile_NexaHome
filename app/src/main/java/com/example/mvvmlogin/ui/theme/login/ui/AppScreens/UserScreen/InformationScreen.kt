package com.example.mvvmlogin.ui.theme.login.ui.AppScreens.UserScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mvvmlogin.R
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.BottomMenu
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.Screen




@Composable
fun InformationScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.0f))


        SocialMediaIcon(icon = R.drawable.instagram, url = "https://www.instagram.com/nexa.home_/", description = "Instagram: nexa.home_")
        Spacer(modifier = Modifier.height(16.dp)) // Espaciador entre Instagram y TikTok
        SocialMediaIcon(icon = R.drawable.tiktok, url = "https://www.tiktok.com/@nexa.home_", description = "TikTok: nexa.home_")
        Spacer(modifier = Modifier.height(16.dp)) // Espaciador entre TikTok y Twitter
        SocialMediaIcon(icon = R.drawable.twitter, url = "https://twitter.com/NexaHome_", description = "Twitter: NexaHome_")

        Spacer(modifier = Modifier.weight(1.0f))
        BottomMenu(selectedScreen = Screen.Profile, onScreenSelected = { /* No hacer nada */ }, navController = navController)
    }
}

@Composable
fun SocialMediaIcon(icon: Int, url: String, description: String) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = description, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(15.dp)) // Espaciador entre el texto y la imagen
        Box(
            modifier = Modifier
                .size(100.dp) // Tamaño deseado para los íconos
                .clickable {
                    openUrlInBrowser(url, context)
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(500.dp) // Tamaño del ícono dentro del contenedor
            )
        }
    }
}

fun openUrlInBrowser(url: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

