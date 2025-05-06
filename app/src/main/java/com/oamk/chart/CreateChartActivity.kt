package com.oamk.chart

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.oamk.chart.ui.theme.ChartTheme

class CreateChartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensures that the system UI does not overlay the content
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val chartType = intent.getStringExtra("CHART_TYPE") ?: "Unknown"
        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues()),
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
    // State list for the columns, each column has X and Y values
    val columns = remember {
        mutableStateListOf(
            Pair(
                mutableStateOf(TextFieldValue("")),
                mutableStateOf(TextFieldValue(""))
            )
        )
    }

    // Get the current context to start new activities
    val context = LocalContext.current

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

        // Display labels for X and Y axis
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("X Label", fontSize = 16.sp, modifier = Modifier.weight(1f))
            Text("Y Label", fontSize = 16.sp, modifier = Modifier.weight(1f))
        }

        // Loop through each column, displaying a pair of X and Y label input fields
        columns.forEachIndexed { index, pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = pair.first.value,
                    onValueChange = { newValue ->
                        pair.first.value = newValue
                    },
                    label = { Text("X${index + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = pair.second.value,
                    onValueChange = { newValue ->
                        pair.second.value = newValue
                    },
                    label = { Text("Y${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                // Delete button
                if (columns.size > 1) {
                    Button(
                        onClick = { columns.removeAt(index) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }

        Button(
            onClick = {
                columns.add(
                    Pair(
                        mutableStateOf(TextFieldValue("")),
                        mutableStateOf(TextFieldValue(""))
                    )
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("+ Add Column")
        }

        // Button to add a new column
        Button(
            onClick = {
                val xLabels = columns.map { it.first.value.text }
                val yLabels = columns.map { it.second.value.text }

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
