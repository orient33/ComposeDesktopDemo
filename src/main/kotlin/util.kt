// simple calculate
const val TAB_CAL = 0

// tools for dump memory info with adb shell
const val TAB_MEM = 1

// tools to push SystemUI.apk with adb shell
const val TAB_SYS = 2

const val TAB_ALL = 3

fun tabName(tab: Int): String {
    return when (tab) {
        TAB_CAL -> "Calculate"
        TAB_MEM -> "Dump Memory"
        else -> "SystemUI tools"
    }
}

fun String.isAllNumber(): Boolean {
    var isDigit = true
    this.forEach {
        isDigit = isDigit && it.isDigit()
    }
    return isDigit
}