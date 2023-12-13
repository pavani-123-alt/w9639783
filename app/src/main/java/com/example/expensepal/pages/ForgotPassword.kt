package com.example.expensepal.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.expensepal.R
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnrememberedMutableState")
@Composable
fun ForgotPassword(
    onLogin: () -> Unit = {},
    onRegistered: () -> Unit = {},
    modifier: Modifier = Modifier.background(color = Color.White)
){

    var username by rememberSaveable { mutableStateOf("") }

    val isValidate by derivedStateOf { username.isNotBlank()}
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    // Firebase Authentication instance
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = modifier.padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween

    ) {
        val imagePainter: Painter = painterResource(id = R.drawable.logotransparent)
        val checkedState = remember { mutableStateOf(true) }

        Image(
            painter = imagePainter,
            contentDescription = "Image Content Description",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 40.dp)
        )
//        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        Column {
            EditNumberField(
                label = R.string.email,
                leadingIcon = R.drawable.ic_email,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                value = username,
                onValueChanged = { username = it },
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                VisualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .weight(1f, false)
                    .padding(dimensionResource(R.dimen.padding_medium)),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            if (isValidate) {
                                resetPassword(username,onLogin,context)
                            }
                        },
                        enabled = isValidate
                    ) {
                        Text(stringResource(R.string.reset))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick ={
                onRegistered()
            }
            ) {
                Text(text = "Don't have account? Sign Up")
            }
        }

    }

}

private fun isEmailValid(email: String): Boolean {
    val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return emailRegex.matches(email)
}

private fun resetPassword(email: String,onLogin: () -> Unit,context: Context) {

    if (!isEmailValid(email)) {
        Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
        return
    }

    val auth = FirebaseAuth.getInstance()
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Password reset email sent successfully.", Toast.LENGTH_SHORT).show()
                onLogin()
            } else {
                Toast.makeText(context,"Failed to send reset email. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}





