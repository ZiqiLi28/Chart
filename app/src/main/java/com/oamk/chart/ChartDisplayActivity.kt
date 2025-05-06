package com.oamk.chart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.oamk.chart.ui.theme.ChartTheme
import com.patrykandpatrick.vico.multiplatform.cartesian.*
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer

class ChartDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensures that the system UI does not overlay the content
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val chartTitle = intent.getStringExtra("CHART_TITLE") ?: "Chart"
        val chartType = intent.getStringExtra("CHART_TYPE") ?: "Unknown"
        val xLabels = intent.getStringArrayListExtra("X_LABELS") ?: arrayListOf()
        val yLabels = intent.getStringArrayListExtra("Y_LABELS") ?: arrayListOf()

        val yValues = yLabels.mapNotNull { it.toFloatOrNull() }

        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChartDisplayScreen(chartTitle, chartType, xLabels, yValues)
                }
            }
        }
    }
}

@Composable
fun ChartDisplayScreen(
    title: String,
    chartType: String,
    xLabels: List<String>,
    yValues: List<Float>
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(yValues) {
        modelProducer.runTransaction {
            when (chartType) {
                "BarChart" -> columnSeries { series(yValues.map { it.toDouble() }) }
                "LineChart" -> lineSeries { series(yValues.map { it.toDouble() }) }
            }
        }
    }

    // Layout for displaying the chart and title
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Based on the chart type, display either a Bar Chart or Line Chart
        when (chartType) {
            "BarChart" -> CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, value, _ ->
                            val index = value.toInt()
                            if (index in xLabels.indices) xLabels[index] else value.toString()
                        }
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            "LineChart" -> CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, value, _ ->
                            val index = value.toInt()
                            if (index in xLabels.indices) xLabels[index] else value.toString()
                        }
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            else -> Text(
                text = "Unsupported chart type",
                color = MaterialTheme.colorScheme.error
            )
        }
        Text(text = title, fontSize = 24.sp)
    }
}
