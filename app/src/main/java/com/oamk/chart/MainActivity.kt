// First page for users choose chart

package com.oamk.chart

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.oamk.chart.ui.theme.ChartTheme
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensures that the system UI does not overlay the content
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(onChartSelected = { chartType ->
                        val intent = when (chartType) {
                            "ParametrizedCurve" -> Intent(this, CreateParametrizedCurveActivity::class.java)
                            "FunctionPlot" -> Intent(this, CreateFunctionPlotActivity::class.java)
                            "Exponential" -> Intent(this, CreateExponentialRegressionActivity::class.java)
                            "Quadratic" -> Intent(this, CreateQuadraticPlotActivity::class.java)
                            "ScatterPlot" -> Intent(this, CreateScatterPlotActivity::class.java)
                            "LinearRegression" -> Intent(this, CreateLinearRegressionActivity::class.java)
                            "PosNegBarChart" -> Intent(this, CreatePosNegChartActivity::class.java)
                            "BarChart", "LineChart" -> Intent(this, CreateBarLineChartActivity::class.java).apply {
                                putExtra("CHART_TYPE", chartType)
                            }
                            else -> null
                        }
                        intent?.let { startActivity(it) }
                    })
                }
            }
        }
    }
}

@Composable
fun MainScreen(onChartSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select a Chart Type", fontSize = 24.sp)
        // Button for selecting bar chart
        ChartButton(text = "Bar Chart", onClick = { onChartSelected("BarChart") })
        // Button for selecting line chart
        ChartButton(text = "Line Chart", onClick = { onChartSelected("LineChart") })
        // Button for positive negative bar chart
        ChartButton(text = "Pos‑Neg Bar Chart", onClick = { onChartSelected("PosNegBarChart") })
        // Button for Scatter Plot
        ChartButton(text = "Scatter Plot", onClick = { onChartSelected("ScatterPlot") })
        // Button for Linear Regression
        ChartButton(text = "Linear Regression", onClick = { onChartSelected("LinearRegression") })
        // Button for Quadratic Regression
        ChartButton(text = "Quadratic Regression", onClick = { onChartSelected("Quadratic") })
        // Button for Exponential Regression
        ChartButton(text = "Exponential Regression", onClick = { onChartSelected("Exponential") })
        // Button for Function Plot
        ChartButton(text = "Function Plotting",
            onClick = { onChartSelected("FunctionPlot") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)))
        ChartButton(text = "Parametrized Curve",
            onClick = { onChartSelected("ParametrizedCurve") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)))
    }
}

@Composable
fun ChartButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = colors,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}
