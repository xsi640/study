package desgin

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


interface Count {
    fun queryCount()
    fun updateCount()
}

class CountImpl : Count {
    override fun queryCount() {
        println("query count.")
    }

    override fun updateCount() {
        println("update count.")
    }

}

class CountProxy : InvocationHandler {

    private var target: Any? = null

    fun bind(target: Any): Any {
        this.target = target

        return Proxy.newProxyInstance(target::class.java.classLoader, target::class.java.interfaces, this)
    }


    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
        var result: Any? = null

        println("预处理操作——————")

        //调用真正的业务方法
        if (method.parameterCount == 0) {
            result = method.invoke(target)
        } else {
            result = method.invoke(target, args)
        }


        println("调用后处理——————")
        return result
    }
}

fun main(args: Array<String>) {
    val count = CountImpl()
    val proxy = CountProxy()
    val c = proxy.bind(count) as Count
    c.updateCount()
    c.queryCount()
}