import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mem.mylog

class Model {
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
    private val isMem = MutableStateFlow(false)
    val mem: StateFlow<Boolean> = isMem

    private val pkgNameL = MutableStateFlow("")
    val pkgName: StateFlow<String> = pkgNameL
    fun switchFunc() {
        val old = mem.value
        isMem.tryEmit(!old)
    }

    fun updatePackageName(value: String) {
        pkgNameL.tryEmit(value)
    }

}