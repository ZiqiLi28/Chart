package com.oamk.chart

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oamk.chart.ui.theme.ChartTheme
import androidx.core.view.WindowCompat

class CreateParametrizedCurveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ChartTheme {
                Surface(modifier = Modifier.fillMaxSize(). padding(WindowInsets.safeDrawing.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background) {
                    ParametrizedCurveInputScreen { tMin, tMax, xExpr, yExpr ->
                        val intent = Intent(this, DisplayParametrizedCurveActivity::class.java).apply {
                            putExtra("T_MIN", tMin)
                            putExtra("T_MAX", tMax)
                            putExtra("X_EXPR", xExpr)
                            putExtra("Y_EXPR", yExpr)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun ParametrizedCurveInputScreen(onSubmit: (Float, Float, String, String) -> Unit) {
    var tMin by remember { mutableStateOf("") }
    var tMax by remember { mutableStateOf("") }
    var xExpr by remember { mutableStateOf("") }
    var yExpr by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = tMin, onValueChange = { tMin = it }, label = { Text("tMin") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = tMax, onValueChange = { tMax = it }, label = { Text("tMax") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = xExpr, onValueChange = { xExpr = it }, label = { Text("x(t) expression") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = yExpr, onValueChange = { yExpr = it }, label = { Text("y(t) expression") })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val tMinFloat = tMin.toFloatOrNull() ?: 0f
            val tMaxFloat = tMax.toFloatOrNull() ?: 1f
            onSubmit(tMinFloat, tMaxFloat, xExpr, yExpr)
        }) {
            Text("Plot Curve")
        }
    }
}
