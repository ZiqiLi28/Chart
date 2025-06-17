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
import com.oamk.chart.ui.theme.ChartTheme
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.Argument
import androidx.core.view.WindowCompat

class DisplayFunctionPlotActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val expr = intent.getStringExtra("FUNCTION_EXPR") ?: "x"
        val xMin = intent.getFloatExtra("X_MIN", -10f)
        val xMax = intent.getFloatExtra("X_MAX", 10f)

        setContent {
            ChartTheme {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background) {
                    DisplayFunction(expr, xMin, xMax)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DisplayFunction(exprText: String, xMin: Float, xMax: Float) {
    val expr = remember { Expression(exprText) }
    val argX = remember { Argument("x = 0") }
    expr.addArguments(argX)

    val points = remember {
        val list = mutableListOf<Pair<Float, Float>>()
        val step = (xMax - xMin) / 500f
        var x = xMin
        while (x <= xMax) {
            argX.argumentValue = x.toDouble()
            val y = expr.calculate().toFloat()
            if (!y.isNaN()) {
                list.add(Pair(x, y))
            }
            x += step
        }
        list
    }

    val yMin = points.minOfOrNull { it.second } ?: -10f
    val yMax = points.maxOfOrNull { it.second } ?: 10f

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Function: f(x) = $exprText")
        Spacer(modifier = Modifier.height(16.dp))

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) {

            val w = size.width
            val h = size.height

            val xStep = ((xMax - xMin) / 10).coerceAtLeast(1f)
            val yStep = ((yMax - yMin) / 10).coerceAtLeast(1f)

            fun mapX(x: Float) = (x - xMin) / (xMax - xMin) * w
            fun mapY(y: Float) = h - (y - yMin) / (yMax - yMin) * h

            drawLine(Color.Black, Offset(mapX(0f), 0f), Offset(mapX(0f), h), 2f)
            drawLine(Color.Black, Offset(0f, mapY(0f)), Offset(w, mapY(0f)), 2f)

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

            val path = Path()
            points.firstOrNull()?.let { (x, y) ->
                path.moveTo(mapX(x), mapY(y))
            }
            for ((x, y) in points.drop(1)) {
                path.lineTo(mapX(x), mapY(y))
            }
            drawPath(path = path, color = Color.Blue, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
        }
    }
}
