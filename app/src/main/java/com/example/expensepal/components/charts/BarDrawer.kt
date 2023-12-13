package com.example.expensepal.components.charts

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.github.tehras.charts.bar.BarChartData
import com.example.expensepal.models.Recurrence
import com.example.expensepal.ui.theme.FillTertiary
import com.example.expensepal.ui.theme.md_theme_light_primary

class BarDrawer constructor(recurrence: Recurrence) :
  com.github.tehras.charts.bar.renderer.bar.BarDrawer {
  private val barPaint = Paint().apply {
    this.isAntiAlias = true
  }

  private val rightOffset = when(recurrence) {
    Recurrence.Weekly -> 24f
    Recurrence.Monthly -> 6f
    Recurrence.Yearly -> 18f
    else -> 0f
  }

  override fun drawBar(
    drawScope: DrawScope,
    canvas: Canvas,
    barArea: Rect,
    bar: BarChartData.Bar
  ) {
    canvas.drawRoundRect(
      barArea.left,
      0f,
      barArea.right + rightOffset,
      barArea.bottom,
      16f,
      16f,
      barPaint.apply {
        color = FillTertiary
      },
    )
    canvas.drawRoundRect(
      barArea.left,
      barArea.top,
      barArea.right + rightOffset,
      barArea.bottom,
      16f,
      16f,
      barPaint.apply {
        color = md_theme_light_primary
      },
    )
  }
}