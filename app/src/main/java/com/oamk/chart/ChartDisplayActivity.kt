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
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer

class ChartDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chartTitle = intent.getStringExtra("CHART_TITLE") ?: "Chart"
        val chartType  = intent.getStringExtra("CHART_TYPE")  ?: "Unknown"
        val yLabels    = intent.getStringArrayListExtra("Y_LABELS") ?: arrayListOf()

        // 只取 Y 值，忽略无法解析的条目
        val yValues = yLabels.mapNotNull { it.toFloatOrNull() }

        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MaterialTheme.colorScheme.background
                ) {
                    ChartDisplayScreen(chartTitle, chartType, yValues)
                }
            }
        }
    }
}

@Composable
fun ChartDisplayScreen(
    title:     String,
    chartType: String,
    yValues:   List<Float>
) {
    Column(
        modifier           = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(text = title, fontSize = 24.sp)

        when (chartType) {
            "BarChart" -> {
                // 1) 创建数据生产者
                val modelProducer = remember { CartesianChartModelProducer() }
                // 2) 在 LaunchedEffect 中异步提交 columnSeries
                LaunchedEffect(yValues) {
                    modelProducer.runTransaction {
                        columnSeries {
                            series(*yValues.toFloatArray())
                        }
                    }
                }
                // 3) 渲染柱状图
                CartesianChartHost(
                    chart         = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis  = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom()
                    ),
                    modelProducer = modelProducer,
                    modifier      = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
            "LineChart" -> {
                val modelProducer = remember { CartesianChartModelProducer() }
                LaunchedEffect(yValues) {
                    modelProducer.runTransaction {
                        lineSeries {
                            series(*yValues.toFloatArray())
                        }
                    }
                }
                CartesianChartHost(
                    chart         = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        startAxis  = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom()
                    ),
                    modelProducer = modelProducer,
                    modifier      = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
            else -> {
                Text(
                    text  = "Unsupported chart type",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}