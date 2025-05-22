package com.oamk.chart

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.oamk.chart.ui.theme.ChartTheme

class ScatterPlotDisplayActivity : ComponentActivity() {
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

@Composable
fun ScatterPlotWithRegression(
    title: String,
    xValues: List<Float>,
    yValues: List<Float>
) {
    val xMean = xValues.average().toFloat()
    val yMean = yValues.average().toFloat()
    val m = xValues.zip(yValues).fold(0f) { acc, (x, y) ->
        acc + (x - xMean) * (y - yMean)
    } / xValues.fold(0f) { acc, x -> acc + (x - xMean).let { it * it } }
    val b = yMean - m * xMean

    val xMin = xValues.minOrNull() ?: 0f
    val xMax = xValues.maxOrNull() ?: 1f
    val yMin = yValues.minOrNull() ?: 0f
    val yMax = yValues.maxOrNull() ?: 1f

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
            fun mapY(y: Float) = h - ( (y - yMin) / (yMax - yMin) * h )

            // draw points
            xValues.zip(yValues).forEach { (x, y) ->
                drawCircle(
                    color = Color.Blue,
                    radius = 6f,
                    center = Offset(mapX(x), mapY(y))
                )
            }

            // draw regression line from xMin to xMax
            val start = Offset(mapX(xMin), mapY(m * xMin + b))
            val end   = Offset(mapX(xMax), mapY(m * xMax + b))
            drawLine(
                color = Color.Red,
                strokeWidth = 4f,
                start = start,
                end = end
            )
        }
    }
}
