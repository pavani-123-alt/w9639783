package com.example.expensepal.ui
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensepal.R
import com.example.expensepal.pages.Add
import com.example.expensepal.pages.Categories
import com.example.expensepal.pages.ChangePasswordScreen
import com.example.expensepal.pages.Expenses
import com.example.expensepal.pages.Profile
import com.example.expensepal.pages.Reports
import com.example.expensepal.pages.SendMessageScreen
import com.example.expensepal.pages.Settings
import com.example.expensepal.ui.theme.BottomAppBarBackground
import com.google.firebase.auth.FirebaseAuth
import io.sentry.compose.withSentryObservableEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogin: () -> Unit = {},
    modifier: Modifier = Modifier.background(color = Color.White)
){
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    val navController = rememberNavController().withSentryObservableEffect()
    val backStackEntry by navController.currentBackStackEntryAsState()

    showBottomBar = when (backStackEntry?.destination?.route) {
        "settings/categories" -> false
        else -> true
    }
    showBottomBar = when (backStackEntry?.destination?.route) {
        "settings/profile" -> false
        else -> true
    }

    val mAuth = FirebaseAuth.getInstance()
    val currentUserUid = mAuth.currentUser?.uid


    // Log the current UID
    Log.d("HomeScreen", "Current UID: $currentUserUid")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = BottomAppBarBackground) {
                    NavigationBarItem(
                        selected = backStackEntry?.destination?.route == "expenses",
                        onClick = { navController.navigate("expenses") },
                        label = {
                            Text(
                                "Expenses"
                                , color = Color.White
                            )
                        },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.upload),
                                contentDescription = "Upload"
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = backStackEntry?.destination?.route == "reports",
                        onClick = { navController.navigate("reports") },
                        label = {
                            Text("Reports",color = Color.White)
                        },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.bar_chart),
                                contentDescription = "Reports"
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = backStackEntry?.destination?.route == "add",
                        onClick = { navController.navigate("add") },
                        label = {
                            Text("Add",color = Color.White)
                        },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.add),
                                contentDescription = "Add"
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = backStackEntry?.destination?.route?.startsWith("settings")
                            ?: false,
                        onClick = { navController.navigate("settings") },
                        label = {
                            Text("Settings",color = Color.White)
                        },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.settings_outlined),
                                contentDescription = "Settings"
                            )
                        }
                    )
                }
            }
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "expenses"
            ) {
                composable("expenses") {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        Expenses(navController)
                    }
                }
                composable("reports") {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        Reports()
                    }
                }
                composable("add") {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        Add(navController)
                    }
                }
                composable("settings") {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        Settings(onLogin = onLogin,navController)
                    }
                }
                composable("settings/categories") {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        Categories(navController)
                    }
                }
                composable("settings/changePassword") {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        ChangePasswordScreen(navController)
                    }
                }
                composable("settings/sendMessage") {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        SendMessageScreen(userId = currentUserUid?: "", navController = navController)
                    }
                }
                composable("settings/profile") {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        Profile(navController)
                    }
                }
            }
        }
    )
}




