package com.example.expensepal.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.expensepal.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onLogin: () -> Unit = {},
    onHome : () -> Unit = {},
    modifier: Modifier = Modifier.background(color = Color.White)
) {
//    Column(
//        Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        val scale = remember {
//            Animatable(0f)
//        }
//
//        val overshootInterpolator = remember {
//            OvershootInterpolator(1.5f)
//        }
//
//        LaunchedEffect(key1 = true) {
//            withContext(Dispatchers.Main) {
//                scale.animateTo(
//                    targetValue = 1.0f,
//                    animationSpec = tween(
//                        durationMillis = 500,
//                        easing = {
//                            overshootInterpolator.getInterpolation(it)
//                        }
//                    )
//                )
//                delay(1000)
//                onLogin()
//            }
//        }
//
//        Image(
//            modifier = Modifier.padding(24.dp),
//            painter = painterResource(id = R.drawable.logo),
//            contentDescription = null
//        )
//    }

    val mAuth = FirebaseAuth.getInstance()

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        val currentUser = mAuth.currentUser

        scale.animateTo(
            targetValue = 0.7f,
            // tween Animation
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }))
        // Customize the delay time
        delay(2000L)
        // Check if the user is already logged in
        if (currentUser != null) {
            // User is logged in, navigate to HomeScreen
            onHome()
        } else {
            // User is not logged in, navigate to LoginScreen
            onLogin.invoke()
        }
    }
    // Animation
//    LaunchedEffect(key1 = true) {
//        scale.animateTo(
//            targetValue = 0.7f,
//            // tween Animation
//            animationSpec = tween(
//                durationMillis = 800,
//                easing = {
//                    OvershootInterpolator(4f).getInterpolation(it)
//                }))
//        // Customize the delay time
//        delay(3000L)
//        onLogin()
//    }

    // Image
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        // Change the logo
        Image(painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value))
    }
}
