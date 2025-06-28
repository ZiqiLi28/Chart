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
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.oamk.chart.ui.theme.ChartTheme

class CreateParametrizedCurveActivity : ComponentActivity() {
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
                    CreateParametrizedCurveScreen()
                }
            }
        }
    }
}

@Composable
fun CreateParametrizedCurveScreen() {
    val context = LocalContext.current

    var tMinInput by remember { mutableStateOf(TextFieldValue("0")) }
    var tMaxInput by remember { mutableStateOf(TextFieldValue("3.14")) }
    var xExprInput by remember { mutableStateOf(TextFieldValue("cos(t)")) }
    var yExprInput by remember { mutableStateOf(TextFieldValue("sin(t)")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Enter Parametrized Curve (x(t), y(t))", fontSize = 20.sp)

        OutlinedTextField(
            value = xExprInput,
            onValueChange = { xExprInput = it },
            label = { Text("x(t) =") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = yExprInput,
            onValueChange = { yExprInput = it },
            label = { Text("y(t) =") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = tMinInput,
                onValueChange = { tMinInput = it },
                label = { Text("t Min") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = tMaxInput,
                onValueChange = { tMaxInput = it },
                label = { Text("t Max") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                val tMin = tMinInput.text.toFloatOrNull()
                val tMax = tMaxInput.text.toFloatOrNull()
                if (tMin == null || tMax == null || tMin >= tMax) {
                    Toast.makeText(context, "Invalid t range", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (xExprInput.text.isBlank() || yExprInput.text.isBlank()) {
                    Toast.makeText(context, "Expressions cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val intent = Intent(context, DisplayParametrizedCurveActivity::class.java).apply {
                    putExtra("X_EXPR", xExprInput.text)
                    putExtra("Y_EXPR", yExprInput.text)
                    putExtra("T_MIN", tMin)
                    putExtra("T_MAX", tMax)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Draw Curve")
        }
    }
}
