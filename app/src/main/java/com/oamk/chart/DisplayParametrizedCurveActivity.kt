package com.oamk.chart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.oamk.chart.ui.theme.ChartTheme
import org.mariuszgromada.math.mxparser.Argument
import org.mariuszgromada.math.mxparser.Expression
import kotlin.math.hypot

class DisplayParametrizedCurveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val xExprText = intent.getStringExtra("X_EXPR") ?: "cos(t)"
        val yExprText = intent.getStringExtra("Y_EXPR") ?: "sin(t)"
        val tMin = intent.getFloatExtra("T_MIN", 0f)
        val tMax = intent.getFloatExtra("T_MAX", 3.1415f)

        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayParametrizedCurve(xExprText, yExprText, tMin, tMax)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DisplayParametrizedCurve(xExprText: String, yExprText: String, tMin: Float, tMax: Float) {
    val xExpr = remember { Expression(xExprText) }
    val yExpr = remember { Expression(yExprText) }
    val argT = remember { Argument("t = 0") }

    xExpr.addArguments(argT)
    yExpr.addArguments(argT)

    val points = remember {
        val list = mutableListOf<Pair<Float, Float>>()
        val step = (tMax - tMin) / 500f
        var t = tMin
        while (t <= tMax) {
            argT.argumentValue = t.toDouble()
            val x = xExpr.calculate().toFloat()
            val y = yExpr.calculate().toFloat()
            if (!x.isNaN() && !y.isNaN()) {
                list.add(Pair(x, y))
            }
            t += step
        }
        list
    }

    val xMin = points.minOfOrNull { it.first } ?: -1f
    val xMax = points.maxOfOrNull { it.first } ?: 1f
    val yMin = points.minOfOrNull { it.second } ?: -1f
    val yMax = points.maxOfOrNull { it.second } ?: 1f

    val totalLength = remember {
        points.zipWithNext { (x1, y1), (x2, y2) -> hypot(x2 - x1, y2 - y1) }.sum()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("x(t) = $xExprText")
        Text("y(t) = $yExprText")
        Text(String.format("Curve Length: %.4f", totalLength))
        Spacer(modifier = Modifier.height(16.dp))

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) {

            val w = size.width
            val h = size.height

            val dataWidth = xMax - xMin
            val dataHeight = yMax - yMin

            // Calculate uniform scale for both axes (same pixels per unit for X and Y)
            val scaleX = w / dataWidth
            val scaleY = h / dataHeight
            val scale = minOf(scaleX, scaleY)

            // Calculate offset to center the plot
            val offsetX = (w - scale * dataWidth) / 2f
            val offsetY = (h - scale * dataHeight) / 2f

            fun mapX(x: Float) = offsetX + (x - xMin) * scale
            fun mapY(y: Float) = h - offsetY - (y - yMin) * scale

            // Draw X and Y axes
            if (0f in xMin..xMax) {
                val xZero = mapX(0f)
                drawLine(Color.Black, Offset(xZero, 0f), Offset(xZero, h), 2f)
            }
            if (0f in yMin..yMax) {
                val yZero = mapY(0f)
                drawLine(Color.Black, Offset(0f, yZero), Offset(w, yZero), 2f)
            }

            // Draw X-axis ticks and labels
            val xStep = ((xMax - xMin) / 10).coerceAtLeast(0.1f)
            var xTick = (xMin / xStep).toInt() * xStep
            while (xTick <= xMax) {
                val px = mapX(xTick)
                drawLine(Color.Gray, Offset(px, mapY(0f) - 5), Offset(px, mapY(0f) + 5), 1f)
                drawContext.canvas.nativeCanvas.drawText(
                    String.format("%.1f", xTick),
                    px,
                    mapY(0f) + 25,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
                xTick += xStep
            }

            // Draw Y-axis ticks and labels
            val yStep = ((yMax - yMin) / 10).coerceAtLeast(0.1f)
            var yTick = (yMin / yStep).toInt() * yStep
            while (yTick <= yMax) {
                val py = mapY(yTick)
                drawLine(Color.Gray, Offset(mapX(0f) - 5, py), Offset(mapX(0f) + 5, py), 1f)
                drawContext.canvas.nativeCanvas.drawText(
                    String.format("%.1f", yTick),
                    mapX(0f) - 10,
                    py + 10,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
                yTick += yStep
            }

            // Draw the curve
            val path = Path()
            points.firstOrNull()?.let { (x, y) ->
                path.moveTo(mapX(x), mapY(y))
            }
            for ((x, y) in points.drop(1)) {
                path.lineTo(mapX(x), mapY(y))
            }
            drawPath(path = path, color = Color.Red, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
        }
    }
}
