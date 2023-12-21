package adbtool

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


//var disposable: Disposable? = null
val model = ToolModel()

@Composable
fun toolRoot() {
    val d = model.url.collectAsState()
    val state = model.state.collectAsState()
    Column {
//        Text("adb tools")
        Row {
            TextField(
                d.value,
                label = { Text("device ip:5555") },
                onValueChange = {
                    model.updateUrl(it)
                }
            )
            Button(
                onClick = { model.connect() }
            ) {
                Text("链接")
            }
            Text(state.value)
        }
        // end adb connect.
        pushView()
    }
}

@Composable
fun pushView() {
    val logText = model.logText.collectAsState("")
    val out = model.sysApkPath.collectAsState()
    Column {

        TextField(
            out.value,
            onValueChange = { v -> model.updateApkPath(v) },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row {
            Button(
                onClick = {
                    model.pushKillSystemUiPad()
                },
                modifier = Modifier.padding(10.dp).padding(0.dp)
            ) {
                Text("push+kill SystemUI")
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
