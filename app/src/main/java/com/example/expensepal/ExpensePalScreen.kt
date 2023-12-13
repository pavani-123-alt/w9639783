package com.example.expensepal

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensepal.ui.ForgotPassword
import com.example.expensepal.ui.HomeScreen
import com.example.expensepal.ui.LoginScreen
import com.example.expensepal.ui.Registration
import com.example.expensepal.ui.SplashScreen


enum class ExpensePalScreen(@StringRes val title: Int) {
    Splash(title = R.string.Splash),
    Login(title = R.string.login),
    Registration(title = R.string.registered),
    ForgotPassword(title = R.string.ForgotPassword),
    Home(title = R.string.home),
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensePalAppBar(
    currentScreen: ExpensePalScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

//    if (currentScreen == ExpensePalScreen.Home) {
//        TopAppBar(
//            title = { Text(stringResource(currentScreen.title)) },
//            colors = TopAppBarDefaults.mediumTopAppBarColors(
//                containerColor = MaterialTheme.colorScheme.primaryContainer
//            ),
//            modifier = modifier,
//        )
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensePalApp(
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = ExpensePalScreen.valueOf(
        backStackEntry?.destination?.route ?: ExpensePalScreen.Login.name
    )

    Scaffold(
        topBar = {
            ExpensePalAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = ExpensePalScreen.Splash.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = ExpensePalScreen.Splash.name) {
                val context = LocalContext.current
                SplashScreen(
                    onLogin = {
                        navController.navigate(ExpensePalScreen.Login.name)
                    },
                    onHome = {
                        navController.navigate(ExpensePalScreen.Home.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = ExpensePalScreen.Registration.name) {
                val context = LocalContext.current
                Registration(
                    onLoginButtonClicked = {
                        navController.navigate(ExpensePalScreen.Login.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = ExpensePalScreen.Login.name) {
                val context = LocalContext.current
                LoginScreen(
                    onLoginButtonClicked = {
                        navController.navigate(ExpensePalScreen.Home.name)
                    },
                    onRegistered = {
                        navController.navigate(ExpensePalScreen.Registration.name)
                    },
                    onForgotPassword = {
                        navController.navigate(ExpensePalScreen.ForgotPassword.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = ExpensePalScreen.ForgotPassword.name) {
                val context = LocalContext.current
                ForgotPassword(
                    onLogin = {
                        navController.navigate(ExpensePalScreen.Login.name)
                    },
                    onRegistered = {
                        navController.navigate(ExpensePalScreen.Registration.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = ExpensePalScreen.Home.name) {
                val context = LocalContext.current
                HomeScreen(
                    onLogin = {
                        navController.navigate(ExpensePalScreen.Login.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

        }
    }
}

