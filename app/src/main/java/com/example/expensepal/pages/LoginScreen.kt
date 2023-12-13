package com.example.expensepal.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensepal.R
import com.google.firebase.auth.FirebaseAuth



@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen(
    onLoginButtonClicked: () -> Unit = {},
    onRegistered: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    modifier: Modifier = Modifier.background(color = Color.White)
){

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val isValidate by derivedStateOf { username.isNotBlank() && password.isNotBlank() }
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
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
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
                VisualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
            )
            EditNumberField(
                label = R.string.password,
                leadingIcon = R.drawable.ic_login_password,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                value = password,
                onValueChanged = { password = it },
                VisualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick ={
                    onForgotPassword()
                }
                ) {
                    Text(text = "Forgot password?")
                }
            }

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
                            performFirebaseAuthentication(
                                auth = auth,
                                email = username,
                                password = password,
                                context = context,
                                onLoginButtonClicked = onLoginButtonClicked
                            )
                        },
                        enabled = isValidate
                    ) {
                        Text(stringResource(R.string.login))
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
private fun performFirebaseAuthentication(
    auth: FirebaseAuth,
    email: String,
    password: String,
    context: Context,  // Pass the context as a parameter
    onLoginButtonClicked: () -> Unit
) {

    if (!isEmailValid(email)) {
        Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
        return
    }
    if (password.length < 6) {
        Toast.makeText(context, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show()
        return
    }
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Authentication successful, you can navigate to the next screen or perform other actions
                // For example: onLoginButtonClicked()
                Toast.makeText(context, "Authentication successful", Toast.LENGTH_SHORT).show()
                onLoginButtonClicked()
            } else {
                // If authentication fails, display a message to the user
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    VisualTransformation: VisualTransformation,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        singleLine = true,
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null) },
        modifier = modifier,
        onValueChange = onValueChanged,
        visualTransformation = VisualTransformation,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions
    )
}





@Preview
@Composable
fun LoginScreenPreview(){
    LoginScreen (modifier = Modifier.fillMaxHeight() )
}
