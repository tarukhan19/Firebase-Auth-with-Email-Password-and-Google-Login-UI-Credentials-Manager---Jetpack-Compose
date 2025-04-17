package com.demo.authentication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demo.authentication.config.navigation.ScreenRoute
import com.demo.authentication.config.navigation.ScreenRoute.Home
import com.demo.authentication.config.navigation.ScreenRoute.Login
import com.demo.authentication.config.navigation.ScreenRoute.Signup
import com.demo.authentication.config.theme.UserAuthTheme
import com.demo.authentication.core.presentation.viewmodel.SharedViewModel
import com.demo.authentication.userauth.data.repository.CredentialManagementRepositoryImpl
import com.demo.authentication.userauth.presentation.home.HomeScreen
import com.demo.authentication.userauth.presentation.login.LoginScreenRoot
import com.demo.authentication.userauth.presentation.signup.SignUpRoot
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var credentialManagerHelper: CredentialManagementRepositoryImpl

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
fun AppNavigation(sharedViewModel: SharedViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedInState = sharedViewModel.isLoggedIn.collectAsStateWithLifecycle().value
    val isDataLoaded = sharedViewModel.isDataLoaded.collectAsState().value

    Crossfade(targetState = isDataLoaded) { loaded ->
        if (loaded) {
            Log.e("isLoggedInState.value", isLoggedInState.toString())
            val startDestination = if (isLoggedInState) Home else Login

            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable<Login> {
                    LoginScreenRoot(
                        onSignUpNavigate = { navController.navigateToSingleTop(Signup) },
                        onHomeNavigate = { navController.navigateToSingleTop(Home) },
                    )
                }
                composable<Signup> {
                    SignUpRoot(
                        onLogInNavigate = { navController.navigateToSingleTop(Login) },
                    )
                }
                composable<Home> {
                    HomeScreen()
                }
            }
        }
    }
}

fun NavController.navigateToSingleTop(screenRoute: ScreenRoute) {
    this.navigate(screenRoute) {
        popUpTo(Login) { inclusive = false } // Clears all backstack up to start
        launchSingleTop = true
    }
}
