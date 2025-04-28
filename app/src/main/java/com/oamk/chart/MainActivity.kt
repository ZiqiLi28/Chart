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
import com.oamk.chart.ui.theme.ChartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(onChartSelected = { chartType ->
                        val intent = Intent(this, CreateChartActivity::class.java)
                        intent.putExtra("CHART_TYPE", chartType)
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
        ChartButton(text = "Histogram", onClick = { onChartSelected("Histogram") })
        ChartButton(text = "Bar Chart", onClick = { onChartSelected("BarChart") })
        ChartButton(text = "Pie Chart", onClick = { onChartSelected("PieChart") })
        ChartButton(text = "Line Chart", onClick = { onChartSelected("LineChart") })
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
