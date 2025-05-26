package com.oamk.chart

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oamk.chart.ui.theme.ChartTheme
import kotlin.math.pow
import java.util.Locale
import kotlin.math.abs

class DisplayQuadraticPlotActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("CHART_TITLE") ?: "Quadratic Plot"
        val xValues = intent.getFloatArrayExtra("X_VALUES")?.toList() ?: emptyList()
        val yValues = intent.getFloatArrayExtra("Y_VALUES")?.toList() ?: emptyList()

        setContent {
            ChartTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    QuadraticPlot(title, xValues, yValues)
                }
            }
        }
    }
}

@Composable
fun QuadraticPlot(title: String, xValues: List<Float>, yValues: List<Float>) {
    val n = xValues.size
    val sumX = xValues.sum()
    val sumX2 = xValues.sumOf { it.toDouble().pow(2) }.toFloat()
    val sumX3 = xValues.sumOf { it.toDouble().pow(3) }.toFloat()
    val sumX4 = xValues.sumOf { it.toDouble().pow(4) }.toFloat()
    val sumY = yValues.sum()
    val sumXY = xValues.indices.sumOf { xValues[it] * yValues[it] }
    val sumX2Y = xValues.indices.sumOf { xValues[it].toDouble().pow(2) * yValues[it] }.toFloat()

    // Solve normal equations for a*x^2 + b*x + c
    val A = arrayOf(
        floatArrayOf(sumX4, sumX3, sumX2),
        floatArrayOf(sumX3, sumX2, sumX),
        floatArrayOf(sumX2, sumX, n.toFloat())
    )
    val B = floatArrayOf(sumX2Y, sumXY, sumY)

    val coeffs = solve3x3(A, B)
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

            drawLine(Color.Black, Offset(0f, h), Offset(w, h), 2f)
            drawLine(Color.Black, Offset(0f, 0f), Offset(0f, h), 2f)

            // draw points
            xValues.zip(yValues).forEach { (x, y) ->
                drawCircle(Color.Blue, radius = 6f, center = Offset(mapX(x), mapY(y)))
            }

            // draw quadratic curve
            val step = (xMax - xMin) / 100
            var prev = Offset(mapX(xMin), mapY(a * xMin * xMin + b * xMin + c))
            for (x in xMin + step..xMax step step) {
                val y = a * x * x + b * x + c
                val next = Offset(mapX(x), mapY(y))
                drawLine(Color.Red, start = prev, end = next, strokeWidth = 3f)
                prev = next
            }
        }

        Text(
            text = String.format(Locale.US, "y = %.2fx² + %.2fx + %.2f", a, b, c),
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// Gaussian elimination for 3x3
fun solve3x3(A: Array<FloatArray>, B: FloatArray): FloatArray {
    val matrix = Array(3) { FloatArray(4) }
    for (i in 0..2) {
        for (j in 0..2) {
            matrix[i][j] = A[i][j]
        }
        matrix[i][3] = B[i]
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
