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
import com.oamk.chart.ui.theme.ChartTheme
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.bar.barChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.pie.pieChart
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.entry.entriesOf

class ChartDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chartTitle = intent.getStringExtra("CHART_TITLE") ?: "Chart"
        val chartType = intent.getStringExtra("CHART_TYPE") ?: "Unknown"
        val xLabels = intent.getStringArrayListExtra("X_LABELS") ?: arrayListOf()
        val yLabels = intent.getStringArrayListExtra("Y_LABELS") ?: arrayListOf()

        val dataPairs = xLabels.zip(yLabels).mapNotNull { (x, y) ->
            val yValue = y.toFloatOrNull()
            if (yValue != null) entryOf(xLabels.indexOf(x).toFloat(), yValue) else null
        }

        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChartDisplayScreen(chartTitle, chartType, dataPairs)
                }
            }
        }
    }
}

@Composable
fun ChartDisplayScreen(
    chartTitle: String,
    chartType: String,
    data: List<com.patrykandpatrick.vico.core.entry.ChartEntry>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(text = chartTitle, fontSize = 24.sp)

        when (chartType) {
            "Histogram", "BarChart" -> {
                Chart(
                    chart = barChart(),
                    model = entriesOf(data)
                )
            }
            "LineChart" -> {
                Chart(
                    chart = lineChart(),
                    model = entriesOf(data)
                )
            }
            "PieChart" -> {
                Chart(
                    chart = pieChart(),
                    model = entriesOf(data)
                )
            }
            else -> {
                Text(text = "Unsupported chart type", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
