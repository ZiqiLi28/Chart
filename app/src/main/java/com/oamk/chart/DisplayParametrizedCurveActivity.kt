package com.oamk.chart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.oamk.chart.ui.theme.ChartTheme
import org.mariuszgromada.math.mxparser.Argument
import org.mariuszgromada.math.mxparser.Expression
import kotlin.math.hypot
import androidx.core.view.WindowCompat

class DisplayParametrizedCurveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val tMin = intent.getFloatExtra("T_MIN", 0f)
        val tMax = intent.getFloatExtra("T_MAX", 1f)
        val xExprText = intent.getStringExtra("X_EXPR") ?: "cos(t)"
        val yExprText = intent.getStringExtra("Y_EXPR") ?: "sin(t)"

        setContent {
            ChartTheme {
                Surface(modifier = Modifier.fillMaxSize(). padding(WindowInsets.safeDrawing.asPaddingValues()), color = MaterialTheme.colorScheme.background) {
                    DisplayParametrizedCurve(tMin, tMax, xExprText, yExprText)
                }
            }
        }
    }
}

@Composable
fun DisplayParametrizedCurve(tMin: Float, tMax: Float, xExprText: String, yExprText: String) {
    val points = remember {
        val list = mutableListOf<Pair<Float, Float>>()
        val tStep = (tMax - tMin) / 500f
        val argT = Argument("t = 0")
        val xExpr = Expression(xExprText)
        val yExpr = Expression(yExprText)
        xExpr.addArguments(argT)
        yExpr.addArguments(argT)

        var t = tMin
        while (t <= tMax) {
            argT.argumentValue = t.toDouble()
            val x = xExpr.calculate().toFloat()
            val y = yExpr.calculate().toFloat()
            if (!x.isNaN() && !y.isNaN()) {
                list.add(Pair(x, y))
            }
            t += tStep
        }
        list
    }

    val totalLength = remember(points) {
        var length = 0f
        for (i in 1 until points.size) {
            val dx = points[i].first - points[i - 1].first
            val dy = points[i].second - points[i - 1].second
            length += hypot(dx, dy)
        }
        length
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Parametric curve:")
        Text("x(t) = $xExprText")
        Text("y(t) = $yExprText")
        Text("t ∈ [$tMin, $tMax]")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Approximate Length: ${"%.4f".format(totalLength)}")

        Spacer(modifier = Modifier.height(16.dp))
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) {

            val xs = points.map { it.first }
            val ys = points.map { it.second }

            val xMin = xs.minOrNull() ?: -1f
            val xMax = xs.maxOrNull() ?: 1f
            val yMin = ys.minOrNull() ?: -1f
            val yMax = ys.maxOrNull() ?: 1f

            val w = size.width
            val h = size.height

            fun mapX(x: Float) = (x - xMin) / (xMax - xMin) * w
            fun mapY(y: Float) = h - (y - yMin) / (yMax - yMin) * h

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
