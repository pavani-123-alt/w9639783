package com.example.expensepal.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensepal.db
import com.example.expensepal.models.Category
import com.example.expensepal.models.Expense
import com.example.expensepal.models.Recurrence
import com.example.expensepal.models.SaveExpense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class AddScreenState(
  val amount: String = "",
  val recurrence: Recurrence = Recurrence.None,
  val date: LocalDate = LocalDate.now(),
  val note: String = "",
  val category: Category? = null,
  val categories: RealmResults<Category>? = null
)

class AddViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(AddScreenState())
  val uiState: StateFlow<AddScreenState> = _uiState.asStateFlow()

  init {
    _uiState.update { currentState ->
      currentState.copy(
        categories = db.query<Category>().find()
      )
    }
  }

  fun setAmount(amount: String) {
    var parsed = amount.toDoubleOrNull()

    if (amount.isEmpty()) {
      parsed = 0.0
    }

    if (parsed != null) {
      _uiState.update { currentState ->
        currentState.copy(
          amount = amount.trim().ifEmpty { "0" },
        )
      }
    }
  }

  fun setRecurrence(recurrence: Recurrence) {
    _uiState.update { currentState ->
      currentState.copy(
        recurrence = recurrence,
      )
    }
  }

  fun setDate(date: LocalDate) {
    _uiState.update { currentState ->
      currentState.copy(
        date = date,
      )
    }
  }

  fun setNote(note: String) {
    _uiState.update { currentState ->
      currentState.copy(
        note = note,
      )
    }
  }

  fun setCategory(category: Category) {
    _uiState.update { currentState ->
      currentState.copy(
        category = category,
      )
    }
  }


  fun submitExpense() {
    if (_uiState.value.category != null) {
      viewModelScope.launch(Dispatchers.IO) {
        val now = LocalDateTime.now()
        db.write {
          this.copyToRealm(
            Expense(
              _uiState.value.amount.toDouble(),
              _uiState.value.recurrence,
              _uiState.value.date.atTime(now.hour, now.minute, now.second),
              _uiState.value.note,
              this.query<Category>("_id == $0", _uiState.value.category!!._id)
                .find().first(),
            )
          )
        }
        _uiState.update { currentState ->
          currentState.copy(
            amount = "",
            recurrence = Recurrence.None,
            date = LocalDate.now(),
            note = "",
            category = null,
            categories = null
          )
        }
      }
      submitDataExpense()
    }
  }


  fun submitDataExpense() {
    val currentUser = FirebaseAuth.getInstance().currentUser

    currentUser?.let { user ->
      viewModelScope.launch(Dispatchers.IO) {
        val now = LocalDateTime.now()

        val expense = SaveExpense(
          amount = _uiState.value.amount.toDouble(),
          recurrence = _uiState.value.recurrence,
          date = _uiState.value.date.atTime(now.hour, now.minute, now.second),
          note = _uiState.value.note,
          category = _uiState.value.category,
          userId = user.uid
        )

        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("expenses")

        collectionReference
          .add(expense.toMap()) // Convert Expense to Map for Firestore
          .addOnSuccessListener { documentReference ->
            Log.d(ContentValues.TAG, "Expense added with ID: ${documentReference.id}")

            // Clear the UI state after submitting the expense
            _uiState.update { currentState ->
              currentState.copy(
                amount = "",
                recurrence = Recurrence.None,
                date = LocalDate.now(),
                note = "",
                category = null,
                categories = null
              )
            }
          }
          .addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Error adding expense", e)
          }
      }
    }
  }

}

