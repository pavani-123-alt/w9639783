package com.example.expensepal.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensepal.R
import com.example.expensepal.ui.EditNameField
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMessageScreen(
    userId: String,
    navController: NavController
) {
    val context = LocalContext.current
    var feedbackText by remember { mutableStateOf("") }

    // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imagePainter: Painter = painterResource(id = R.drawable.logotransparent)
        Image(
            painter = imagePainter,
            contentDescription = "Image Content Description",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 20.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var emailname by rememberSaveable { mutableStateOf("") }
            OutlinedTextField(
                value = emailname,
                onValueChange = { emailname = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = TextStyle.Default.copy(
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp // Adjust the font size as needed
                ),
            )
            OutlinedTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                label = { Text("Queries") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = TextStyle.Default.copy(
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp // Adjust the font size as needed
                ),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )


            Button(
                onClick = {
                    // Perform feedback submission
                    submitFeedback(db, userId,emailname, feedbackText, navController,context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Submit Feedback")
            }
        }
    }
}

private fun submitFeedback(
    db: FirebaseFirestore,
    userId: String,
    email :String,
    feedbackText: String,
    navController: NavController,
    context:Context
) {
    // Create a data class or map to represent the feedback data
    val feedbackData = mapOf(
        "userId" to userId,
        "EmailId" to email,
        "feedbackText" to feedbackText
    )

    // Add the feedback data to Firestore collection
    db.collection("feedback")
        .add(feedbackData)
        .addOnSuccessListener {
            // Feedback submission successful
            Toast.makeText(
                context,
                "Feedback submitted successfully",
                Toast.LENGTH_SHORT
            ).show()

            // Navigate back to settings screen or any other desired destination
            navController.navigateUp()
        }
        .addOnFailureListener { e ->
            // Feedback submission failed
            Toast.makeText(
                context,
                "Feedback submission failed: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}
