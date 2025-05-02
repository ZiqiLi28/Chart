package com.oamk.chart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.oamk.chart.ui.theme.ChartTheme
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.utils.ColorTemplate

class ChartDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chartTitle = intent.getStringExtra("CHART_TITLE") ?: ""
        val chartType = intent.getStringExtra("CHART_TYPE") ?: ""
        val xLabels = intent.getStringArrayListExtra("X_LABELS") ?: arrayListOf()
        val yLabels = intent.getStringArrayListExtra("Y_LABELS") ?: arrayListOf()

        setContent {
            ChartTheme {
                ChartDisplayScreen(
                    chartTitle = chartTitle,
                    chartType = chartType,
                    xLabels = xLabels,
                    yLabels = yLabels
                )
            }
        }
    }
}

@Composable
fun ChartDisplayScreen(
    chartTitle: String,
    chartType: String,
    xLabels: List<String>,
    yLabels: List<String>
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = chartTitle, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (chartType) {
            "Histogram", "Bar Chart" -> {
                AndroidView(
                    factory = { context ->
                        BarChart(context).apply {
                            val entries = yLabels.mapIndexed { index, y ->
                                BarEntry(index.toFloat(), y.toFloatOrNull() ?: 0f)
                            }
                            val dataSet = BarDataSet(entries, chartTitle).apply {
                                colors = ColorTemplate.MATERIAL_COLORS.toList()
                            }
                            data = BarData(dataSet)
                            xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            axisRight.isEnabled = false
                            description.isEnabled = false
                            invalidate()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )
            }
            "Pie Chart" -> {
                AndroidView(
                    factory = { context ->
                        PieChart(context).apply {
                            val entries = yLabels.mapIndexed { index, y ->
                                PieEntry(y.toFloatOrNull() ?: 0f, xLabels.getOrNull(index) ?: "")
                            }
                            val dataSet = PieDataSet(entries, chartTitle).apply {
                                colors = ColorTemplate.MATERIAL_COLORS.toList()
                            }
                            data = PieData(dataSet)
                            description.isEnabled = false
                            invalidate()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )
            }
            "Line Chart" -> {
                AndroidView(
                    factory = { context ->
                        LineChart(context).apply {
                            val entries = yLabels.mapIndexed { index, y ->
                                Entry(index.toFloat(), y.toFloatOrNull() ?: 0f)
                            }
                            val dataSet = LineDataSet(entries, chartTitle).apply {
                                color = ColorTemplate.getHoloBlue()
                                setCircleColor(ColorTemplate.getHoloBlue())
                            }
                            data = LineData(dataSet)
                            xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            axisRight.isEnabled = false
                            description.isEnabled = false
                            invalidate()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )
            }
            else -> {
                Text(text = "Unsupported chart type: $chartType")
            }
        }
    }
}
