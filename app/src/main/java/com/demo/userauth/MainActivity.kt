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
import com.demo.userauth.data.datastore.UserPreferences
import com.demo.userauth.presentation.home.HomeScreen
import com.demo.userauth.presentation.navigation.ScreenRoute.Login
import com.demo.userauth.presentation.navigation.ScreenRoute.Signup
import com.demo.userauth.presentation.login.LoginScreen
import com.demo.userauth.presentation.navigation.ScreenRoute
import com.demo.userauth.presentation.navigation.ScreenRoute.Home
import com.demo.userauth.presentation.signup.SignupScreen
import com.demo.userauth.presentation.theme.UserAuthTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var dataStoreManager: UserPreferences

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
    val startDestination = Login
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable<Login> {
            LoginScreen(
                onSignUpNavigate = { navHostController.navigateToSingleTop(Signup) },
                onHomeNavigate = { navHostController.navigateToSingleTop(Home) }
            )
        }
        composable<Signup> {
            SignupScreen(
                onLogInNavigate = { navHostController.navigateToSingleTop(Login) }
            )
        }
        composable<Home>{
            HomeScreen()
        }
    }
}

fun NavController.navigateToSingleTop(screenRoute: ScreenRoute) {
    this.navigate(screenRoute) {
        popUpTo(Login) { inclusive = false }  // Clears all backstack up to start
        launchSingleTop = true
    }
}