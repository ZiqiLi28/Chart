package com.oamk.chart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.oamk.chart.ui.theme.ChartTheme
import java.util.Locale
import androidx.compose.ui.Alignment

class DisplayScatterPlotActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val title = intent.getStringExtra("CHART_TITLE") ?: "Scatter Plot"
        val xValues = intent.getFloatArrayExtra("X_VALUES")?.toList() ?: emptyList()
        val yValues = intent.getFloatArrayExtra("Y_VALUES")?.toList() ?: emptyList()

        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScatterPlotWithRegression(title, xValues, yValues)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ScatterPlotWithRegression(
    title: String,
    xValues: List<Float>,
    yValues: List<Float>
) {
    val xMean = xValues.average().toFloat()
    val yMean = yValues.average().toFloat()
    val numerator = xValues.zip(yValues).fold(0f) { acc, (x, y) ->
        acc + (x - xMean) * (y - yMean)
    }
    val denominator = xValues.fold(0f) { acc, x ->
        acc + (x - xMean) * (x - xMean)
    }
    val m = if (denominator == 0f) 0f else numerator / denominator
    val b = yMean - m * xMean

    val xMin = minOf(xValues.minOrNull() ?: 0f, 0f)
    val xMax = maxOf(xValues.maxOrNull() ?: 1f, 0f)
    val yMin = minOf(yValues.minOrNull() ?: 0f, 0f)
    val yMax = maxOf(yValues.maxOrNull() ?: 1f, 0f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = title, fontSize = 24.sp)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val w = size.width
            val h = size.height

            fun mapX(x: Float) = (x - xMin) / (xMax - xMin) * w
            fun mapY(y: Float) = h - (y - yMin) / (yMax - yMin) * h

            val axisPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
                isAntiAlias = true
            }

            val originX = mapX(0f)
            val originY = mapY(0f)

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

                drawLine(Color.Gray, Offset(xPos, originY - 8f), Offset(xPos, originY + 8f), 1f)
                drawContext.canvas.nativeCanvas.drawText(
                    String.format("%.1f", x),
                    xPos,
                    originY + 30f,
                    axisPaint.apply { textAlign = android.graphics.Paint.Align.CENTER }
                )

                drawLine(Color.Gray, Offset(originX - 8f, yPos), Offset(originX + 8f, yPos), 1f)
                drawContext.canvas.nativeCanvas.drawText(
                    String.format("%.1f", y),
                    originX - 10f,
                    yPos + 8f,
                    axisPaint.apply { textAlign = android.graphics.Paint.Align.RIGHT }
                )
            }

            xValues.zip(yValues).forEach { (x, y) ->
                drawCircle(
                    color = Color.Blue,
                    radius = 6f,
                    center = Offset(mapX(x), mapY(y))
                )
            }

            val start = Offset(mapX(xMin), mapY(m * xMin + b))
            val end = Offset(mapX(xMax), mapY(m * xMax + b))
            drawLine(
                color = Color.Red,
                strokeWidth = 4f,
                start = start,
                end = end
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = String.format(Locale.US, "y = %.2fx + %.2f", m, b),
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
