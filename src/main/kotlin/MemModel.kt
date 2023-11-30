import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mem.MemInfo
import mem.mylog
import mem.readMemory
import mem.stopRead

const val STATE_IDLE = 0
const val STATE_START = 1
const val STATE_ERROR = 2

class MemModel {

    private val pkgNameL = MutableStateFlow("thememanager")
    val pkgName: StateFlow<String> = pkgNameL

    fun updatePackageName(value: String) {
        if (state == STATE_IDLE) {
            pkgNameL.tryEmit(value)
            mylog("emit PackageName $value !")
        } else {
            mylog("state not allow change PackageName !")
        }
    }


    private val stateString = MutableStateFlow("click to start")
    val stateBtn: StateFlow<String> = stateString
    var state = STATE_IDLE
    fun clickState() {
        if (state == STATE_IDLE) {
            // start. job
            updateState(STATE_START)
            mylog("doStart--!!")
            doStart()
        } else if (state == STATE_START) {
            updateState(STATE_IDLE)
            doStop()
        }
    }

    private fun updateState(s: Int) {
        state = s
        val desc = when (s) {
            STATE_START -> "working, click to stop"
            STATE_IDLE -> "idle, click to start"
            else -> "error"
        }
        stateString.tryEmit(desc)
    }

    fun doStart() {
//        CoroutineScope("").launch {  }
        GlobalScope.launch(Dispatchers.IO) {
            doStartIn()
        }
    }

    fun doStop() {
        stopRead()
    }

    private val memFlow = MutableStateFlow(mutableListOf(MemInfo(0)))
    val memList: StateFlow<List<MemInfo>> = memFlow

    private suspend fun doStartIn() {
        val ok = readMemory(pkgName.value, memFlow)
        mylog("read end, auto stop ? $ok") //true is click stop.. false is error/maxCount
        updateState(STATE_IDLE)
    }

}
