package com.demo.userauth

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demo.userauth.data.datastore.UserPreferences
import com.demo.userauth.presentation.navigation.ScreenRoute.Login
import com.demo.userauth.presentation.navigation.ScreenRoute.Signup
import com.demo.userauth.presentation.navigation.ScreenRoute
import com.demo.userauth.presentation.navigation.ScreenRoute.Home
import com.demo.userauth.presentation.screen.HomeScreen
import com.demo.userauth.presentation.screen.LoginScreen
import com.demo.userauth.presentation.screen.SignupScreen
import com.demo.userauth.presentation.theme.UserAuthTheme
import com.demo.userauth.presentation.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
fun AppNavigation(
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn = sharedViewModel.isLoggedIn.collectAsState().value
    val startDestination = if(isLoggedIn) Home else Login
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Login> {
            LoginScreen(
                onSignUpNavigate = { navController.navigateToSingleTop(Signup) },
                onHomeNavigate = { navController.navigateToSingleTop(Home) }
            )
        }
        composable<Signup> {
            SignupScreen(
                onLogInNavigate = { navController.navigateToSingleTop(Login) }
            )
        }
        composable<Home> {
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