package com.example.expensepal.mock

import androidx.compose.ui.graphics.Color
import com.example.expensepal.models.Category
import com.example.expensepal.models.Expense
import com.example.expensepal.models.Recurrence
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import io.github.serpro69.kfaker.Faker
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

val faker = Faker()

val mockCategories = listOf(
  Category(
    "Bills",
    Color(
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255)
    )
  ),
  Category(
    "Subscriptions", Color(
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255)
    )
  ),
  Category(
    "Take out", Color(
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255)
    )
  ),
  Category(
    "Hobbies", Color(
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255)
    )
  ),
)

val mAuth = FirebaseAuth.getInstance()
val currentUserUid = mAuth.currentUser?.uid

val mockExpense: List<Expense> = List(30) {
  Expense(
    amount = faker.random.nextInt(min = 1, max = 999)
      .toDouble() + faker.random.nextDouble(),
    date = LocalDateTime.now().minus(
      faker.random.nextInt(min = 300, max = 345600).toLong(),
      ChronoUnit.SECONDS
    ),
    recurrence = faker.random.randomValue(
      listOf(
        Recurrence.None,
        Recurrence.Daily,
        Recurrence.Monthly,
        Recurrence.Weekly,
        Recurrence.Yearly
      )
    ),
    note = faker.australia.animals(),
    category = faker.random.randomValue(mockCategories)
//    userId = "Pavani"
  )
}