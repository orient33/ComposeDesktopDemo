import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import sysui.sysuiToolsView
import adbtool.toolRoot


val vm = Model()

@Composable
@Preview
fun App() {
    val text = vm.tabName.collectAsState()
    val out = vm.text.collectAsState()
    val tab = vm.tab.collectAsState()
    MaterialTheme {
        Column {
            Button(
                onClick = {
                    vm.switchFunc()
                },
                modifier = Modifier.padding(10.dp, 0.dp).padding(0.dp)
            ) {
                Text(text.value)
            }
            when (tab.value) {
                TAB_MEM -> {
                    MemRoot()
                }

                TAB_CAL -> {
                    TextField(
                        out.value.toString(),
                        onValueChange = { v -> vm.updateInput(v) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("${out.value} * 2.75 = ${out.value * 2.75f} ")
                    Text("${out.value} * 3 = ${out.value * 3} ")
                    Text("${out.value} / 2.75 = ${out.value / 2.75f} ")
                    Text("${out.value} / 3 = ${out.value / 3} ")
                }

                TAB_SYS -> {
                    sysuiToolsView()
                }
                TAB_TOOL -> {
                    toolRoot()
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "几个工具"
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
    val tableData = (1..10).mapIndexed { index, _ ->
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