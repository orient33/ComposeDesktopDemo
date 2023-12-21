import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

// simple calculate
const val TAB_CAL = 0

// tools for dump memory info with adb shell
const val TAB_MEM = 1

// tools to push SystemUI.apk with adb shell
const val TAB_SYS = 2

const val TAB_TOOL = 3

const val TAB_ALL = 4

fun tabName(tab: Int): String {
    return when (tab) {
        TAB_CAL -> "Calculate"
        TAB_MEM -> "Dump Memory"
        TAB_SYS -> "SystemUI tools"
        TAB_TOOL -> "adb tools"
        else -> "unknown"
    }
}

fun String.isAllNumber(): Boolean {
    var isDigit = true
    this.forEach {
        isDigit = isDigit && it.isDigit()
    }
    return isDigit
}

fun logg(msg: String) {
    println("$msg\n")
}

public object  MyCoroutineScope : CoroutineScope{
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}