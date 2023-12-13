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
import androidx.compose.ui.unit.dp
import com.example.expensepal.R
import com.example.expensepal.models.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@SuppressLint("UnrememberedMutableState")
@Composable
fun Registration(
    onLoginButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier.background(color = Color.White)
){
    var name by rememberSaveable { mutableStateOf("") }
    var emailname by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmpassword by rememberSaveable { mutableStateOf("") }

    val isValidate by derivedStateOf { name.isNotBlank() &&emailname.isNotBlank() && password.isNotBlank() }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
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
                .padding(top = 20.dp)
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
        Column {
            EditNameField(
                label = R.string.name,
                leadingIcon = R.drawable.ic_user,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                value = name,
                onValueChanged = { name = it },
                VisualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
            )
            EditNameField(
                label = R.string.email,
                leadingIcon = R.drawable.ic_email,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                value = emailname,
                onValueChanged = { emailname = it },
                VisualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
            )
            EditNameField(
                label = R.string.password,
                leadingIcon = R.drawable.ic_login_password,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                value = password,
                onValueChanged = { password = it },
                VisualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
            )
            EditNameField(
                label = R.string.confirm_password,
                leadingIcon = R.drawable.ic_login_password,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                value = confirmpassword,
                onValueChanged = { confirmpassword = it },
                VisualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
            )
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
                            performFirebaseRegistration(auth, email= emailname, password = password,confirmpassword,name, context , onRegistered = onLoginButtonClicked)
                        },
                        enabled = isValidate
                    ) {
                        Text("Sign In")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            TextButton(onClick ={
                onLoginButtonClicked()
            }
            ) {
                Text(text = "Already have an account? Sign In?")
            }
        }
    }

}

private fun isEmailValid(email: String): Boolean {
    val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return emailRegex.matches(email)
}

private fun performFirebaseRegistration(
    auth: FirebaseAuth,
    email: String,
    password: String,
    confirmpassword:String,
    name: String,
    context: Context,
    onRegistered: () -> Unit
) {

    if (!isEmailValid(email)) {
        Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
        return
    }
    if (password.length < 6) {
        Toast.makeText(context, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show()
        return
    }
    if (password != confirmpassword) {
        Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid
                // Create a Firestore document for the user
                uid?.let {
                    val userData = UserData(name = name, email = email, password = password, image = R.drawable.ic_user.toString())
                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            // Registration and data storage successful
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                            onRegistered()
                        }
                        .addOnFailureListener {
                            // Registration successful, but data storage failed
                            Toast.makeText(context, "Data storage failed", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                val exception = task.exception
                // If registration fails, display a message to the user
                Toast.makeText(context, "Registration failed ${exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}


//private fun performFirebaseRegistration(
//    auth: FirebaseAuth,
//    email: String,
//    password: String,
//    context: Context,
//    onRegistered: () -> Unit
//) {
//    auth.createUserWithEmailAndPassword(email, password)
//        .addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // Registration successful, you can navigate to the next screen or perform other actions
//                // For example: onRegistered()
//                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
//                onRegistered()
//            } else {
//                // If registration fails, display a message to the user
//                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
//            }
//        }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNameField(
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    VisualTransformation : VisualTransformation,
    modifier: Modifier = Modifier
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

