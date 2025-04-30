package com.oamk.chart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oamk.chart.ui.theme.ChartTheme

class CreateChartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chartType = intent.getStringExtra("CHART_TYPE") ?: "Unknown"
        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreateChartScreen(chartType)
                }
            }
        }
    }
}

@Composable
fun CreateChartScreen(chartType: String) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var columns by remember {
        mutableStateOf(
            listOf(
                Pair(TextFieldValue(""), TextFieldValue(""))
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Chart Type: $chartType", fontSize = 18.sp)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Chart Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("X Label", fontSize = 16.sp, modifier = Modifier.weight(1f))
            Text("Y Label", fontSize = 16.sp, modifier = Modifier.weight(1f))
        }

        columns.forEachIndexed { index, pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = pair.first,
                    onValueChange = {
                        columns = columns.toMutableList().also {
                            it[index] = it[index].copy(first = it)
                        }
                    },
                    label = { Text("X${index + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = pair.second,
                    onValueChange = {
                        columns = columns.toMutableList().also {
                            it[index] = it[index].copy(second = it)
                        }
                    },
                    label = { Text("Y${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Button(
            onClick = {
                columns = columns + Pair(TextFieldValue(""), TextFieldValue(""))
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("+ Add Column")
        }

        Button(
            onClick = {
                val context = LocalContext.current

                val xLabels = columns.map { it.first.text }
                val yLabels = columns.map { it.second.text }

                val intent = Intent(context, ChartDisplayActivity::class.java).apply {
                    putExtra("CHART_TITLE", title.text)
                    putExtra("CHART_TYPE", chartType)
                    putStringArrayListExtra("X_LABELS", ArrayList(xLabels))
                    putStringArrayListExtra("Y_LABELS", ArrayList(yLabels))
                }

                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Generate Chart")
        }
    }
}
