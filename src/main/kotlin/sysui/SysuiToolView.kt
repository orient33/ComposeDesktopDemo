package sysui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

val model = SysModel()

@Composable
fun sysuiToolsView() {
    val logText = model.logText.collectAsState("")
    val out = model.sysApkPath.collectAsState()
    Column {

        TextField(
            out.value,
            onValueChange = { v -> model.updateApkPath(v) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row {
            Button(
                onClick = {
                    model.pushSystemUiPad()
                },
                modifier = Modifier.padding(10.dp).padding(0.dp)
            ) {
                Text("push SystemUI")
            }

            Button(
                onClick = {
                    model.killSystemUI()
                },
                modifier = Modifier.padding(10.dp).padding(0.dp)
            ) {
                Text("kill SystemUI")
            }
        }
        Text(
            text = logText.value
        )
    }
}