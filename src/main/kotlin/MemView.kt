import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mem.MemInfo

val memModel = MemModel()

@Composable
fun MemRoot() {
    val pkgText = memModel.pkgName.collectAsState()
    val btnText = memModel.stateBtn.collectAsState()
    val memList = memModel.memList.collectAsState()

    Row {
        TextField(pkgText.value,
            label = { Text("packageName") },
            onValueChange = {
                memModel.updatePackageName(it)
            })
        Button(onClick = {
            memModel.clickState()
        },
            modifier = Modifier.alignByBaseline()
        ) {
            Text(btnText.value)
        }
    }
    memoryList(memList.value)
}

@Composable
fun memoryList(memList: List<MemInfo>) {
    Row {
        Text("totalPss", modifier = Modifier.weight(1f))
        Text("nativeHeap", modifier = Modifier.weight(1f))
        Text("javaHeap", modifier = Modifier.weight(1f))
        Text("code", modifier = Modifier.weight(1f))
        Text("stack", modifier = Modifier.weight(1f))
        Text("graphics", modifier = Modifier.weight(1f))
        Text("private", modifier = Modifier.weight(1f))
        Text("system", modifier = Modifier.weight(1f))
    }
    LazyColumn (){
        items(memList) { mem ->
            memoryRow(mem)
        }
    }
}

@Composable
fun memoryRow(m: MemInfo) {
    Row {
        Text("${m.totalPss}", modifier = Modifier.weight(1f))
        Text("${m.nativeHeap}", modifier = Modifier.weight(1f))
        Text("${m.javaHeap}", modifier = Modifier.weight(1f))
        Text("${m.code}", modifier = Modifier.weight(1f))
        Text("${m.stack}", modifier = Modifier.weight(1f))
        Text("${m.graphics}", modifier = Modifier.weight(1f))
        Text("${m.privateOther}", modifier = Modifier.weight(1f))
        Text("${m.system}", modifier = Modifier.weight(1f))
    }
}