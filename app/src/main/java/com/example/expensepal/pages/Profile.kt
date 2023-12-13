package com.example.expensepal.pages

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.expensepal.R
import com.example.expensepal.models.UserData
import com.github.skydoves.colorpicker.compose.*
import com.example.expensepal.ui.theme.*
import com.example.expensepal.viewmodels.CategoriesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    navController: NavController
) {

    val mAuth = FirebaseAuth.getInstance()

    // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()

    // State to hold user data
    var currentUserData by remember { mutableStateOf<UserData?>(null) }

    // Check if the user is logged in and retrieve the UID
    mAuth.currentUser?.let { user ->
        val currentUserUid = user.uid
        // Fetch user data from Firestore
        db.collection("users")
            .document(currentUserUid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Convert Firestore document to UserData
                    val userData = document.toObject(UserData::class.java)
                    if (userData != null) {
                        // Update the state with user data
                        currentUserData = userData
                        Log.d("Profile", "User Data: ${userData.name}")
                    }
                } else {
                    Log.d("Profile", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Profile", "Error getting documents: ", exception)
            }
    }

    val currentUserName = currentUserData?.name
    val email = currentUserData?.email
    val image = currentUserData?.image


    Scaffold(topBar = {
        TopAppBar(title = { Text("Profile") },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = TopAppBarBackground
            ),
            navigationIcon = {
                Surface(
                    onClick = navController::popBackStack,
                    color = Color.Transparent,
                ) {
                    Row(modifier = Modifier.padding(vertical = 10.dp)) {
                        Icon(
                            Icons.Rounded.KeyboardArrowLeft, contentDescription = "Settings"
                        )
                        Text("Settings")
                    }
                }
            })
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            if (image != null) {
                if (currentUserName != null) {
                    if (email != null) {
                        EditProfile(
                            imageUri = image,
                            currentUserName = currentUserName,
                            email = email,
                            navController = navController
                        )
                    }
                }
            }
        }
    })
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(
    imageUri :String,
    currentUserName: String,
    email :String,
    navController: NavController
) {
    val context = LocalContext.current

    var editedName by remember { mutableStateOf(currentUserName) }
    var editedemail by remember { mutableStateOf(email) }


    val isValidate by derivedStateOf { currentUserName.isNotBlank() && email.isNotBlank() && imageUri.isNotBlank() }

    // State to hold the selected image URI
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Create a launcher for the image picker
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = uri
        }
    }

    // Coroutine scope for launching image picker and other async tasks
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Profile Image
        Image(
            painter = rememberImagePainter(data = selectedImageUri ?: imageUri, builder = {
                transformations(CircleCropTransformation())
            }),
            contentDescription = null,
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .border(2.dp, color = Color.Gray, shape = CircleShape)
                .clickable {
                    // Launch the image picker
                    pickImage.launch("image/*")
                }
        )

        Spacer(modifier = Modifier.width(10.dp))

        // User Info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = editedName,
                singleLine = true,
                shape = Shapes.large,
                onValueChange = { editedName = it},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = Surface),
                label = { Text(text = stringResource(id = R.string.name))},
                isError = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {}
                )
            )
            OutlinedTextField(
                value = editedemail,
                singleLine = true,
                shape = Shapes.large,
                onValueChange = {editedemail = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = Surface),
                label = { Text(text = stringResource(id = R.string.email))},
                isError = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {}
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {
                    updateUserData(selectedImageUri, editedName, editedemail, context )
                    Log.d("Save","Save")
                    navController.navigate("Settings")
                },
                enabled = isValidate
            ) {
                Text(stringResource(R.string.update))
            }
        }
    }
}

private fun updateUserData(imageUri: Uri?, name: String, email: String, context: Context) {

     val mAuth = FirebaseAuth.getInstance()
     val currentUserUid = mAuth.currentUser?.uid
     val db = FirebaseFirestore.getInstance()
     currentUserUid?.let { uid ->
         db.collection("users").document(uid).update(
             mapOf(
                 "name" to name,
                 "email" to email,
                 "image" to imageUri?.toString()
             )
         ).addOnSuccessListener {
             // Update successful
             Log.d("EditProfile", "User data updated successfully.")
             Toast.makeText(context, "User data updated successfully.", Toast.LENGTH_SHORT).show()

         }.addOnFailureListener {
             // Update failed
             Log.e("EditProfile", "Error updating user data.", it)
             Toast.makeText(context, "Error updating user data.", Toast.LENGTH_SHORT).show()
         }
     }
}
