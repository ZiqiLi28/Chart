// Display positive negative bar chart

package com.oamk.chart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.oamk.chart.ui.theme.ChartTheme
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.*

class PosNegChartDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val chartTitle = intent.getStringExtra("CHART_TITLE") ?: ""
        val xLabels = intent.getStringArrayListExtra("X_LABELS") ?: arrayListOf()
        val yAdjusted = intent.getFloatArrayExtra("Y_ADJUSTED")?.toList() ?: emptyList()

        setContent {
            ChartTheme {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PosNegChartScreen(chartTitle, xLabels, yAdjusted)
                }
            }
        }
    }
}

@Composable
fun PosNegChartScreen(
    title: String,
    xLabels: List<String>,
    yAdjusted: List<Float>
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(yAdjusted) {
        modelProducer.runTransaction {
            columnSeries {
                series(x = xLabels.mapIndexed { i, _ -> i.toDouble() },
                    y = yAdjusted.map { it.toDouble() })
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(text = title, fontSize = 24.sp)

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis  = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, value, _ ->
                        xLabels.getOrNull(value.toInt()) ?: value.toString()
                    }
                )
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxWidth().height(300.dp)
        )
    }
}
