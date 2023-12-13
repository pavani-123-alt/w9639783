package com.example.expensepal.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.sharp.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.expensepal.R
import com.example.expensepal.components.TableRow
import com.example.expensepal.db
import com.example.expensepal.models.Category
import com.example.expensepal.models.Expense
import com.example.expensepal.models.UserData
import com.example.expensepal.ui.theme.BackgroundElevated
import com.example.expensepal.ui.theme.DividerColor
import com.example.expensepal.ui.theme.Ocean1
import com.example.expensepal.ui.theme.Shadow1
import com.example.expensepal.ui.theme.Shadow10
import com.example.expensepal.ui.theme.Shapes
import com.example.expensepal.ui.theme.TopAppBarBackground
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
  onLogin: () -> Unit = {},
  navController: NavController
) {

  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var deleteConfirmationShowing by remember {
    mutableStateOf(false)
  }

  val eraseAllData: () -> Unit = {
    coroutineScope.launch {
      db.write {
        val expenses = this.query<Expense>().find()
        val categories = this.query<Category>().find()

        delete(expenses)
        delete(categories)

        deleteConfirmationShowing = false
      }
    }
  }

  val mAuth = FirebaseAuth.getInstance()

  // Firebase Firestore instance
  val db = FirebaseFirestore.getInstance()

  // State to hold user data
  var currentUserData by remember { mutableStateOf<UserData?>(null) }

  // Check if the user is logged in and retrieve the UID
  mAuth.currentUser?.let { user ->
    val currentUserUid = user.uid

    // Log the current UID
    Log.d("Settings", "Current UID: $currentUserUid")

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
            Log.d("Settings", "User Data: ${userData.name}")

          }
        } else {
          Log.d("Settings", "No such document")
        }
      }
      .addOnFailureListener { exception ->
        Log.d("Settings", "Error getting documents: ", exception)
      }
  }
  val currentUserName = currentUserData?.name
  val imageuri = currentUserData?.image

  // Log the current UID
  Log.d("Setting ", "Current : $currentUserName")

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Settings") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
          containerColor = TopAppBarBackground
        )
      )
    },
    content = { innerPadding ->
      Column(modifier = Modifier.padding(innerPadding)) {

        Spacer(modifier = Modifier.height(16.dp))
        if (currentUserName != null && imageuri != null) {
          ProfileSection(currentUserName = currentUserName,imageuri,navController)
        }

        Column(
          modifier = Modifier
            .padding(16.dp)
            .clip(Shapes.large)
            .background(BackgroundElevated)
            .fillMaxWidth()
        ) {
          TableRow(
            label = "Categories",
            hasArrow = true,
            modifier = Modifier.clickable {
              navController.navigate("settings/categories")
            })
          Divider(
            modifier = Modifier
              .padding(start = 16.dp), thickness = 1.dp, color = DividerColor
          )
          TableRow(
            label = "Erase all data",
            isDestructive = true,
            modifier = Modifier.clickable {
              deleteConfirmationShowing = true
            })

          if (deleteConfirmationShowing) {
            AlertDialog(
              onDismissRequest = { deleteConfirmationShowing = false },
              title = { Text("Are you sure?") },
              text = { Text("This action cannot be undone.") },
              confirmButton = {
                TextButton(onClick = eraseAllData) {
                  Text("Delete everything")
                }
              },
              dismissButton = {
                TextButton(onClick = { deleteConfirmationShowing = false }) {
                  Text("Cancel")
                }
              }
            )
          }
        }
        Column(
          modifier = Modifier
            .padding(16.dp)
            .clip(Shapes.large)
            .background(BackgroundElevated)
            .fillMaxWidth()
        ) {
          TableRow(
            label = "Notifications",
            modifier = Modifier.clickable {
            })
          TableRow(
            label = "Change Password",
            modifier = Modifier.clickable {
              navController.navigate("settings/changePassword")
            })
        }
        Column(
          modifier = Modifier
            .padding(16.dp)
            .clip(Shapes.large)
            .background(BackgroundElevated)
            .fillMaxWidth()
        ){
          TableRow(
            label = "Send us a message",
            modifier = Modifier.clickable {
              navController.navigate("settings/sendMessage")
            })
          TableRow(
            label = "Share",
            modifier = Modifier.clickable {
              shareApp(context)
            })

          TableRow(
            label = "Logout",
            isDestructive = true,
            modifier = Modifier.clickable {
              logoutUser(onLogin = onLogin, context )
            })
        }
      }
    }
  )
}



private fun logoutUser(onLogin: () -> Unit,context: Context) {
  val mAuth = FirebaseAuth.getInstance()

  // Sign out the current user
  mAuth.signOut()
  Toast.makeText(context,"Logout Successfully...",Toast.LENGTH_SHORT).show()
  onLogin()
}

@Composable
fun ProfileSection(
  currentUserName: String,
  imageuri :String,
  navController: NavController
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Profile Image
    Image(
      painter = rememberImagePainter(data = imageuri,  builder = {
        transformations(CircleCropTransformation())
      }),
      contentDescription = null,
      modifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
        .border(2.dp, color = Color.Gray, shape = CircleShape)
    )

    Spacer(modifier = Modifier.width(10.dp))

    // User Info
    Column(
      modifier = Modifier.weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = currentUserName,
        style = MaterialTheme.typography.headlineLarge
      )
      Button(
        onClick = {
          navController.navigate("settings/Profile")
        },
      ) {
        Text (
          text = "Edit Profit",
          style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = null,
        )
      }
    }
  }
}


private fun shareApp(context: Context) {
  // Create an Intent to share text
  val sendIntent: Intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, "Check out this awesome app!")
    type = "text/plain"
  }

  // Start the activity to share the text
  context.startActivity(Intent.createChooser(sendIntent, "Share via"))
}
