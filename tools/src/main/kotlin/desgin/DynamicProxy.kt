package desgin

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

interface Task<T> {
    fun setData(data: String)
    fun getCalData(x: Int): Int
}

interface StringTask : Task<String>

//class TaskImpl : Task {
//    override fun setData(data: String) {
//        println("$data Data is saved")
//    }
//
//    override fun getCalData(x: Int): Int {
//        return x * 10
//    }
//}

class DynamicInvocationHandler(private val methodInvoker: (clazz: Class<*>, method: Method, args: Array<Any>) -> Any?) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        return methodInvoker(proxy::class.java, method, args)
    }
}

object ProxyFactory {
    fun newInstance(clazz: KClass<*>, methodInvoker: (clazz: Class<*>, method: Method, args: Array<Any>) -> Any): Any? {
        return Proxy.newProxyInstance(clazz.java.classLoader, arrayOf<Class<*>>(clazz.java), DynamicInvocationHandler(methodInvoker))
    }
}

object Test {
    @JvmStatic
    fun main(args: Array<String>) {


//        val task = ProxyFactory.newInstance(Task::class) { clazz: Class<*>, method: Method, args: Array<Any> ->
//            var result: Any = Unit
//            if (method.name == "setData") {
//                println("${args[0]} Data is saved")
//            } else if (method.name == "getCalData") {
//                result = args[0] as Int * 10
//            }
//            result
//        } as Task
//        //task.setData("Test")
//        println(task.getCalData(5))

        val clazz = StringTask::class.java
        if (Task::class.java.isAssignableFrom(clazz)) {
            clazz.genericInterfaces.forEach {
                if ((it as ParameterizedTypeImpl).rawType == Task::class.java) {
                    val c = it.actualTypeArguments.get(0)
                    println(c)
                }
            }
        }
        if (clazz.genericInterfaces.contains(Task::class.java)) {

        }
    }
}