package com.example.expensepal.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

class Expense(): RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId.create()
  var amount: Double = 0.0

  private var _recurrenceName: String = "None"
  val recurrence: Recurrence get() { return _recurrenceName.toRecurrence() }

  private var _dateValue: String = LocalDateTime.now().toString()
  val date: LocalDateTime get() { return LocalDateTime.parse(_dateValue) }

  var note: String = ""
  var category: Category? = null
//  var userId: String = ""

  constructor(
    amount: Double,
    recurrence: Recurrence,
    date: LocalDateTime,
    note: String,
    category: Category,
//    userId: String
  ) : this() {
    this.amount = amount
    this._recurrenceName = recurrence.name
    this._dateValue = date.toString()
    this.note = note
    this.category = category
//    this.userId = userId
  }
}

data class DayExpenses(
  val expenses: MutableList<Expense>,
  var total: Double,
)

fun List<Expense>.groupedByDay(): Map<LocalDate, DayExpenses> {
  val dataMap: MutableMap<LocalDate, DayExpenses> = mutableMapOf()

  this.forEach { expense ->
    val date = expense.date.toLocalDate()

    if (dataMap[date] == null) {
      dataMap[date] = DayExpenses(
        expenses = mutableListOf(),
        total = 0.0
      )
    }

    dataMap[date]!!.expenses.add(expense)
    dataMap[date]!!.total = dataMap[date]!!.total.plus(expense.amount)
  }

  dataMap.values.forEach { dayExpenses ->
    dayExpenses.expenses.sortBy { expense -> expense.date }
  }

  return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Expense>.groupedByDayOfWeek(): Map<String, DayExpenses> {
  val dataMap: MutableMap<String, DayExpenses> = mutableMapOf()

  this.forEach { expense ->
    val dayOfWeek = expense.date.toLocalDate().dayOfWeek

    if (dataMap[dayOfWeek.name] == null) {
      dataMap[dayOfWeek.name] = DayExpenses(
        expenses = mutableListOf(),
        total = 0.0
      )
    }

    dataMap[dayOfWeek.name]!!.expenses.add(expense)
    dataMap[dayOfWeek.name]!!.total = dataMap[dayOfWeek.name]!!.total.plus(expense.amount)
  }

  return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Expense>.groupedByDayOfMonth(): Map<Int, DayExpenses> {
  val dataMap: MutableMap<Int, DayExpenses> = mutableMapOf()

  this.forEach { expense ->
    val dayOfMonth = expense.date.toLocalDate().dayOfMonth

    if (dataMap[dayOfMonth] == null) {
      dataMap[dayOfMonth] = DayExpenses(
        expenses = mutableListOf(),
        total = 0.0
      )
    }

    dataMap[dayOfMonth]!!.expenses.add(expense)
    dataMap[dayOfMonth]!!.total = dataMap[dayOfMonth]!!.total.plus(expense.amount)
  }

  return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Expense>.groupedByMonth(): Map<String, DayExpenses> {
  val dataMap: MutableMap<String, DayExpenses> = mutableMapOf()

  this.forEach { expense ->
    val month = expense.date.toLocalDate().month

    if (dataMap[month.name] == null) {
      dataMap[month.name] = DayExpenses(
        expenses = mutableListOf(),
        total = 0.0
      )
    }

    dataMap[month.name]!!.expenses.add(expense)
    dataMap[month.name]!!.total = dataMap[month.name]!!.total.plus(expense.amount)
  }

  return dataMap.toSortedMap(compareByDescending { it })
}

data class SaveExpense(
  val amount: Double,
  val recurrence: Recurrence,
  val date: LocalDateTime,
  val note: String,
  val category: Category?,// Change the type to SaveCategory
  val userId: String
) {
  // ... other properties and functions ...
//  val userId =  FirebaseAuth.getInstance().currentUser
  fun toMap(): Map<String, Any?> {

    return mapOf(
      "amount" to amount,
      "recurrence" to recurrence.name,
      "date" to date.toString(),
      "note" to note,
      "category" to category!!.toMap(), // Call toMap on the instance of SaveCategory
      "userId" to userId
    )
  }
}




