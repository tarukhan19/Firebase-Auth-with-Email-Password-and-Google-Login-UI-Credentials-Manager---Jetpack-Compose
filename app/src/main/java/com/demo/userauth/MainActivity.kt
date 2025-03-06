package com.demo.userauth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demo.userauth.presentation.navigation.ScreenRoute.Login
import com.demo.userauth.presentation.navigation.ScreenRoute.Signup
import com.demo.userauth.presentation.theme.UserAuthTheme
import com.demo.userauth.presentation.login.LoginScreen
import com.demo.userauth.presentation.navigation.ScreenRoute
import com.demo.userauth.presentation.signup.SignupScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserAuthTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(navHostController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navHostController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                onSignUpNavigate = { navHostController.navigateToSingleTop(Signup) }
            )
        }
        composable<Signup> {
            SignupScreen(
                onLogInNavigate = { navHostController.navigateToSingleTop(Login) }
            )
        }
    }
}

fun NavController.navigateToSingleTop(screenRoute: ScreenRoute) {
    this.navigate(screenRoute) {
        popUpTo(Login) { inclusive = false }  // Clears all backstack up to start
        launchSingleTop = true
    }
}