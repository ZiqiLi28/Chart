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
                            "PosNegBarChart" ->
                                Intent(this, CreatePosNegChartActivity::class.java)
                            else ->
                                Intent(this, CreateBarLineChartActivity::class.java).apply {
                                    putExtra("CHART_TYPE", chartType)
                                }
                        }
                        startActivity(intent)
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
    }
}

@Composable
fun ChartButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}
