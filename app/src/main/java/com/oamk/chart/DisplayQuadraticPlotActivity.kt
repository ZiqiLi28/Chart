package com.oamk.chart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oamk.chart.ui.theme.ChartTheme
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import androidx.core.view.WindowCompat

class DisplayQuadraticPlotActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val title = intent.getStringExtra("CHART_TITLE") ?: "Quadratic Plot"
        val xValues = intent.getFloatArrayExtra("X_VALUES")?.toList() ?: emptyList()
        val yValues = intent.getFloatArrayExtra("Y_VALUES")?.toList() ?: emptyList()

        setContent {
            ChartTheme {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background) {
                    QuadraticPlot(title, xValues, yValues)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun QuadraticPlot(title: String, xValues: List<Float>, yValues: List<Float>) {
    val n = xValues.size
    val sumX = xValues.sum()
    val sumX2 = xValues.sumOf { it.toDouble().pow(2) }.toFloat()
    val sumX3 = xValues.sumOf { it.toDouble().pow(3) }.toFloat()
    val sumX4 = xValues.sumOf { it.toDouble().pow(4) }.toFloat()
    val sumY = yValues.sum()
    val sumXY = xValues.indices.sumOf { (xValues[it] * yValues[it]).toDouble() }.toFloat()
    val sumX2Y = xValues.indices.sumOf { xValues[it].toDouble().pow(2) * yValues[it] }.toFloat()

    val aMatrix = arrayOf(
        floatArrayOf(sumX4, sumX3, sumX2),
        floatArrayOf(sumX3, sumX2, sumX),
        floatArrayOf(sumX2, sumX, n.toFloat())
    )
    val bVector = floatArrayOf(sumX2Y, sumXY, sumY)

    val coeffs = solve3x3(aMatrix, bVector)
    val a = coeffs[0]
    val b = coeffs[1]
    val c = coeffs[2]

    val xMin = xValues.minOrNull() ?: 0f
    val xMax = xValues.maxOrNull() ?: 1f
    val yMin = yValues.minOrNull() ?: 0f
    val yMax = yValues.maxOrNull() ?: 1f

    Column(Modifier.padding(16.dp)) {
        Text(text = title, fontSize = 24.sp)

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) {

            val w = size.width
            val h = size.height

            fun mapX(x: Float) = (x - xMin) / (xMax - xMin) * w
            fun mapY(y: Float) = h - ((y - yMin) / (yMax - yMin) * h)

            val axisPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
                isAntiAlias = true
            }

            val originX = mapX(xMin)
            val originY = mapY(yMin)

            drawLine(
                color = Color.Black,
                start = Offset(originX, 0f),
                end = Offset(originX, h),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.Black,
                start = Offset(0f, originY),
                end = Offset(w, originY),
                strokeWidth = 2f
            )

            val xStep = (xMax - xMin) / 5
            val yStep = (yMax - yMin) / 5
            for (i in 0..5) {
                val x = xMin + i * xStep
                val y = yMin + i * yStep
                val xPos = mapX(x)
                val yPos = mapY(y)

                drawLine(
                    color = Color.Gray,
                    start = Offset(xPos, originY - 8f),
                    end = Offset(xPos, originY + 8f),
                    strokeWidth = 1f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    String.format("%.1f", x),
                    xPos,
                    originY + 30f,
                    axisPaint.apply { textAlign = android.graphics.Paint.Align.CENTER }
                )

                drawLine(
                    color = Color.Gray,
                    start = Offset(originX - 8f, yPos),
                    end = Offset(originX + 8f, yPos),
                    strokeWidth = 1f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    String.format("%.1f", y),
                    originX - 10f,
                    yPos + 8f,
                    axisPaint.apply { textAlign = android.graphics.Paint.Align.RIGHT }
                )
            }

            // Draw points
            xValues.zip(yValues).forEach { (x, y) ->
                drawCircle(Color.Blue, radius = 6f, center = Offset(mapX(x), mapY(y)))
            }

            // Draw quadratic regression curve
            val step = (xMax - xMin) / 100
            var x = xMin
            var prev = Offset(mapX(x), mapY(a * x * x + b * x + c))

            while (x <= xMax) {
                val y = a * x * x + b * x + c
                val next = Offset(mapX(x), mapY(y))
                drawLine(Color.Red, start = prev, end = next, strokeWidth = 3f)
                prev = next
                x += step
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = String.format(Locale.US, "y = %.2fx² + %.2fx + %.2f", a, b, c),
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

fun solve3x3(aMatrix: Array<FloatArray>, bVector: FloatArray): FloatArray {
    val matrix = Array(3) { FloatArray(4) }
    for (i in 0..2) {
        for (j in 0..2) {
            matrix[i][j] = aMatrix[i][j]
        }
        matrix[i][3] = bVector[i]
    }

    for (i in 0..2) {
        val maxRow = (i..2).maxByOrNull { abs(matrix[it][i]) }!!
        matrix[i] = matrix[maxRow].also { matrix[maxRow] = matrix[i] }

        val divisor = matrix[i][i]
        for (j in i..3) matrix[i][j] /= divisor

        for (k in i + 1..2) {
            val factor = matrix[k][i]
            for (j in i..3) matrix[k][j] -= factor * matrix[i][j]
        }
    }

    for (i in 2 downTo 0) {
        for (j in i + 1..2) {
            matrix[i][3] -= matrix[i][j] * matrix[j][3]
        }
    }

    return floatArrayOf(matrix[0][3], matrix[1][3], matrix[2][3])
}
