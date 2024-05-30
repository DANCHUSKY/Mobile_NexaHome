// MainActivity.kt
package com.example.mvvmlogin

import LoginScreen
import UserScreen
import UserTokenHolder.token
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvvmlogin.ui.theme.MVVMLoginTheme
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.AddProperty
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.HomeScreen
import com.example.mvvmlogin.ui.theme.login.ui.Login.RegisterScreen
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.SearchScreen
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.UserScreen.InformationScreen
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.UserScreen.PasswordScreen
import com.example.mvvmlogin.ui.theme.login.ui.AppScreens.UserScreen.UserProperty

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVVMLoginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(navController = navController)
                        }
                        composable("register") {

                            RegisterScreen(navController = navController)
                        }
                        composable("home") {

                            HomeScreen(navController = navController)
                        }
                        composable("user") {

                            UserScreen(navController = navController)
                        }
                        composable("search") {

                            SearchScreen(navController = navController)
                        }
                        composable("password") {

                            PasswordScreen(navController = navController)
                        }
                        composable("property") {
                            AddProperty(navController = navController, token = "$token")
                        }

                        composable("userproperty") {

                            UserProperty(navController = navController)
                        }
                        composable("informationscreen") {

                            InformationScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
