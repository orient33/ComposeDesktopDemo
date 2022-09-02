import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

const val DEFAULT_TITLE = "calculator DP - PX"

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf(DEFAULT_TITLE) }
    val vm = Model()
    val out = vm.text.collectAsState()
    val mem = vm.mem.collectAsState()
    MaterialTheme {
        Column {
            Button(onClick = {
                text = if (!mem.value) "dump Memory" else DEFAULT_TITLE
                vm.switchFunc()
            }) {
                Text(text)
            }
            if (mem.value) {
                MemRoot()
            } else {
                TextField(
                    out.value.toString(),
                    onValueChange = { v -> vm.updateInput(v) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text("${out.value} * 2.75 = ${out.value * 2.75f} ")
                Text("${out.value} * 3 = ${out.value * 3} ")
                Text("${out.value} / 2.75 = ${out.value / 2.75f} ")
                Text("${out.value} / 3 = ${out.value / 3} ")
//            TableScreen()
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeDesktop1"
    ) {
        App()
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Text(
        text,
        Modifier.border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun TableScreen() {
    val tableData = (1..10).mapIndexed { index, i ->
        index to "Item $index"
    }
    val c1 = .3f
    val c2 = .7f
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Row {
                Row(Modifier.background(Color.Gray)) {
                    TableCell("column 1", c1)
                    TableCell("column 2", c2)
                }
            }
        }
        items(tableData) {
            val (id, text) = it
            Row(Modifier.fillMaxWidth()) {
                TableCell(id.toString(), c1)
                TableCell(text, c2)
            }
        }
    }
}