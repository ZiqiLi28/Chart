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

class CreateLinearRegressionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ChartTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreateLinearRegressionScreen()
                }
            }
        }
    }
}

@Composable
fun CreateLinearRegressionScreen() {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    val points = remember {
        mutableStateListOf(
            Pair(mutableStateOf(TextFieldValue("")), mutableStateOf(TextFieldValue("")))
        )
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Line Regression Curve", fontSize = 20.sp)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Chart Title") },
            modifier = Modifier.fillMaxWidth()
        )

        points.forEachIndexed { idx, pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = pair.first.value,
                    onValueChange = { pair.first.value = it },
                    label = { Text("X${idx + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = pair.second.value,
                    onValueChange = { pair.second.value = it },
                    label = { Text("Y${idx + 1}") },
                    modifier = Modifier.weight(1f)
                )
                if (points.size > 1) {
                    Button(
                        onClick = { points.removeAt(idx) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }

        Button(
            onClick = {
                points.add(
                    Pair(
                        mutableStateOf(TextFieldValue("")),
                        mutableStateOf(TextFieldValue(""))
                    )
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("+ Add Point")
        }

        Button(
            onClick = {
                // parse floats
                val xVals = points.map { it.first.value.text.toFloatOrNull() }
                val yVals = points.map { it.second.value.text.toFloatOrNull() }
                if (xVals.any { it == null } || yVals.any { it == null }) {
                    Toast.makeText(context, "Enter valid numeric values", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                // all non-null
                val xList = xVals.filterNotNull()
                val yList = yVals.filterNotNull()
                if (xList.size < 2) {
                    Toast.makeText(context, "Need at least 2 points", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // launch display
                val intent = Intent(context, DisplayLinearRegressionActivity::class.java).apply {
                    putExtra("CHART_TITLE", title.text)
                    putExtra("X_VALUES", xList.toFloatArray())
                    putExtra("Y_VALUES", yList.toFloatArray())
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Generate Scatter Plot")
        }
    }
}
