// Create positive negative bar chart

package com.oamk.chart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

class CreatePosNegChartActivity : ComponentActivity() {
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
                    CreatePosNegChartScreen()
                }
            }
        }
    }
}

@Composable
fun CreatePosNegChartScreen() {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    val columns = remember {
        mutableStateListOf(
            Pair(mutableStateOf(TextFieldValue("")), mutableStateOf(TextFieldValue("")))
        )
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Positive‑Negative Bar Chart", fontSize = 20.sp)
        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Chart Title") }, modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Text("X Label Y Value", fontSize = 16.sp)

        columns.forEachIndexed { idx, pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = pair.first.value,
                    onValueChange = { pair.first.value = it },
                    label = { Text("X${idx + 1}") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = pair.second.value,
                    onValueChange = { pair.second.value = it },
                    label = { Text("Y${idx + 1}") },
                    modifier = Modifier.weight(1f)
                )
                if (columns.size > 1) {
                    Button(
                        onClick = { columns.removeAt(idx) },
                        modifier = Modifier.padding(start = 8.dp)
                    ){Text("Delete")}
                }
            }
        }

        Button(onClick = {
            columns.add(
                Pair(mutableStateOf(TextFieldValue("")),
                    mutableStateOf(TextFieldValue("")))
            )
        }, modifier = Modifier.align(Alignment.End)) {
            Text("+ Add Column")
        }

        Button(onClick = {
            val pivotValue = 0f
            val xLabels = columns.map { it.first.value.text }
            val yRaw = columns.map { it.second.value.text.toFloatOrNull() }
            if (yRaw.any { it == null }) {
                Toast.makeText(context, "Enter valid numeric Y values", Toast.LENGTH_SHORT).show()
                return@Button
            }
            val yValues = yRaw.filterNotNull().map { it - pivotValue }

            val intent = Intent(context, DisplayPosNegChartActivity::class.java).apply {
                putExtra("CHART_TITLE", title.text)
                putStringArrayListExtra("X_LABELS", ArrayList(xLabels))
                putExtra("Y_ADJUSTED", yValues.toFloatArray())
            }
            context.startActivity(intent)
        }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
            Text("Generate Pos‑Neg Chart")
        }
    }
}
