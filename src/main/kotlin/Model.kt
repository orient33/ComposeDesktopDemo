import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mem.mylog

const val DEFAULT_TAB = TAB_TOOL
class Model {
    val tabName = MutableStateFlow(tabName(DEFAULT_TAB))
    private val input = MutableStateFlow(0f)
    val text: StateFlow<Float> = input

    fun updateInput(value: String) {
        try {
            val num = value.toFloat()
            input.tryEmit(num)
        } catch (e: Exception) {
            //todo error input
            mylog("error input $value")
        }
    }

    //------mem ------
    private val tabPriv = MutableStateFlow(DEFAULT_TAB)
    val tab: StateFlow<Int> = tabPriv

    private val pkgNameL = MutableStateFlow("")
    val pkgName: StateFlow<String> = pkgNameL
    fun switchFunc() {
        val old = tabPriv.value
        val n = old +1
        val nn = if (n >= TAB_ALL) 0 else n
        tabPriv.tryEmit(nn)
        tabName.tryEmit(
            tabName(nn)
        )
    }

    fun updatePackageName(value: String) {
        pkgNameL.tryEmit(value)
    }

}