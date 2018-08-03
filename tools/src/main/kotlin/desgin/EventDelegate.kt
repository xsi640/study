package desgin

import java.util.*

interface EventDelegate<T> {
    operator fun plusAssign(m: (T) -> Unit)
    operator fun minusAssign(m: (T) -> Unit)

    operator fun invoke(t: T)
}

class Delegate<T> : EventDelegate<T> {
    private val handlers = Vector<(T) -> Unit>()

    override fun plusAssign(m: (T) -> Unit) {
        handlers.add(m)
    }

    override fun minusAssign(m: (T) -> Unit) {
        handlers.remove(m)
    }

    override fun invoke(t: T) {
        for (handler in handlers) {
            handler.invoke(t)
        }
    }
}