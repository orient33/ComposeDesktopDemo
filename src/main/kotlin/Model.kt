import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Model {
    private val input = MutableStateFlow(0f)
    val text: StateFlow<Float> = input

    fun updateInput(value: String) {
        val num = value.toFloat()
        input.tryEmit(num)
    }
}