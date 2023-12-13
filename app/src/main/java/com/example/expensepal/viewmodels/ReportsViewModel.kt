package com.example.expensepal.viewmodels

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensepal.db
import com.example.expensepal.models.Expense
import com.example.expensepal.models.Recurrence
import com.example.expensepal.models.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import com.itextpdf.text.Element
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable

data class ReportsState(
  val recurrence: Recurrence = Recurrence.Weekly,
  val recurrenceMenuOpened: Boolean = false
)

class ReportsViewModel: ViewModel() {
  private val _uiState = MutableStateFlow(ReportsState())
  val uiState: StateFlow<ReportsState> = _uiState.asStateFlow()

  fun setRecurrence(recurrence: Recurrence) {
    _uiState.update { currentState ->
      currentState.copy(
        recurrence = recurrence
      )
    }
  }

  fun openRecurrenceMenu() {
    _uiState.update { currentState ->
      currentState.copy(
        recurrenceMenuOpened = true
      )
    }
  }

  fun closeRecurrenceMenu() {
    _uiState.update { currentState ->
      currentState.copy(
        recurrenceMenuOpened = false
      )
    }
  }

  fun logLocalData() {
    viewModelScope.launch(Dispatchers.IO) {
      val expenses = db.query<Expense>().find()

      if (expenses.isNotEmpty()) {
        for (expense in expenses) {
          Log.d(ContentValues.TAG, "Local expense data: $expense")
          createPdf()
        }
      } else {
        Log.d(ContentValues.TAG, "No local expense data found.")
      }
    }
  }

//  suspend fun createPdf() {
//    withContext(Dispatchers.IO) {
//      val expenses = db.query<Expense>().find()
//
//      if (expenses.isNotEmpty()) {
//        try {
//          val document = Document()
//          val pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/expenses.pdf"
//          val file = File(pdfFilePath)
//
//          val pdfWriter = PdfWriter.getInstance(document, FileOutputStream(file))
//          document.open()
//
//          for (expense in expenses) {
//            document.add(Paragraph("Expense: $expense"))
//            document.add(Paragraph("Amount: ${expense.amount}"))
//            document.add(Paragraph("Recurrence: ${expense.recurrence}"))
//            document.add(Paragraph("Date: ${expense.date}"))
//            document.add(Paragraph("Note: ${expense.note}"))
//            document.add(Paragraph("Category: ${expense.category} "))
//          }
//
//          document.close()
//          pdfWriter.close()
//          Log.d(ContentValues.TAG, "PDF created successfully at: $pdfFilePath")
//        } catch (e: Exception) {
//          Log.e(ContentValues.TAG, "Error creating PDF", e)
//        }
//      } else {
//        Log.d(ContentValues.TAG, "No local expense data found.")
//      }
//    }
//  }


  suspend fun createPdf() {
    withContext(Dispatchers.IO) {
      val expenses = db.query<Expense>().find()

      if (expenses.isNotEmpty()) {
        try {
          val document = Document()
          val pdfFilePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/expenses.pdf"
          val file = File(pdfFilePath)

          val pdfWriter = PdfWriter.getInstance(document, FileOutputStream(file))
          document.open()

          val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f)
          val titleChunk = Chunk("ExpensePal", titleFont)
          val titleParagraph = Paragraph(titleChunk)
          titleParagraph.alignment = Element.ALIGN_CENTER
          document.add(titleParagraph)

          // Add an empty line after the title
          document.add(Paragraph(" "))

//          // Add logo at the top
//          val logoImage = Image.getInstance("C:\\Users\\POOJA\\Desktop\\Project Freelancing\\Final\\ExpensePalApp\\app\\src\\main\\res\\drawable\\logotransparent.png") // Replace with the actual path to your logo
//          logoImage.scaleAbsolute(100f, 100f) // Adjust the size as needed
//          logoImage.alignment = Element.ALIGN_CENTER
//          document.add(logoImage)

          val table = PdfPTable(4) // Number of columns in the table

          // Add table headers
          table.addCell(createCell("Number"))
          table.addCell(createCell("Note"))
          table.addCell(createCell("Date"))
          table.addCell(createCell("Amount"))
//          table.addCell(createCell("Recurrence"))

          var totalAmount = 0.0

          for ((index, expense) in expenses.withIndex()) {
            // Add expense data to the table
            table.addCell(createCell((index + 1).toString()))
            table.addCell(createCell(expense.note))
            table.addCell(createCell(expense.date.toString()))
            table.addCell(createCell(expense.amount.toString()))
//            table.addCell(createCell(expense.recurrence.toString()))

            // Update total amount
            totalAmount += expense.amount
          }

          // Add a row for the total
          table.addCell(createCell("Total"))
          table.addCell(createCell(""))
          table.addCell(createCell(""))
          table.addCell(createCell(totalAmount.toString()))

          document.add(table)

          document.close()
          pdfWriter.close()
          Log.d(ContentValues.TAG, "PDF created successfully at: $pdfFilePath")

        } catch (e: Exception) {
          Log.e(ContentValues.TAG, "Error creating PDF", e)
        }
      } else {
        Log.d(ContentValues.TAG, "No local expense data found.")
      }
    }
  }

  private fun createCell(text: String): PdfPCell {
    val cell = PdfPCell(Paragraph(text))
    cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
  }
}

