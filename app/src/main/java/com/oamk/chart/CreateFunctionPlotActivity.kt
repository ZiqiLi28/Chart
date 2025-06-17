package com.oamk.chart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.oamk.chart.ui.theme.ChartTheme
import androidx.core.view.WindowCompat

class CreateFunctionPlotActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ChartTheme {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background) {
                    CreateFunctionPlotScreen()
                }
            }
        }
    }
}

@Composable
fun CreateFunctionPlotScreen() {
    var functionInput by remember { mutableStateOf(TextFieldValue("")) }
    var xMinInput by remember { mutableStateOf(TextFieldValue("-10")) }
    var xMaxInput by remember { mutableStateOf(TextFieldValue("10")) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Enter a Function (e.g. x^2 + sin(x))")

        OutlinedTextField(
            value = functionInput,
            onValueChange = { functionInput = it },
            label = { Text("f(x) =") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = xMinInput,
                onValueChange = { xMinInput = it },
                label = { Text("X Min") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = xMaxInput,
                onValueChange = { xMaxInput = it },
                label = { Text("X Max") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                val expr = functionInput.text
                val xMin = xMinInput.text.toFloatOrNull()
                val xMax = xMaxInput.text.toFloatOrNull()
                if (xMin == null || xMax == null || xMin >= xMax) {
                    Toast.makeText(context, "Invalid X range", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (expr.isBlank()) {
                    Toast.makeText(context, "Function cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val intent = Intent(context, DisplayFunctionPlotActivity::class.java).apply {
                    putExtra("FUNCTION_EXPR", expr)
                    putExtra("X_MIN", xMin)
                    putExtra("X_MAX", xMax)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Draw Function")
        }
    }
}
