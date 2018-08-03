package desgin

import kotlin.properties.Delegates
import kotlin.reflect.KProperty


interface ValueChangedListener<T> {
    fun onValueChanged(property: KProperty<*>, oldValue: T, newValue: T)
}

class PrintTestValueChangedListener<T> : ValueChangedListener<T> {
    override fun onValueChanged(property: KProperty<*>, oldValue: T, newValue: T) {
        println("11")
    }
}

class ObservableObject(
        val valueChangedListener: ValueChangedListener<String>
) {
    var text: String by Delegates.observable(
            initialValue = "",
            onChange = { prop, old, new ->
                valueChangedListener.onValueChanged(prop, old, new)
            }
    )
}