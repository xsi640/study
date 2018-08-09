package desgin

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.lang.reflect.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface Task<T> {
    fun setData(data: String)
    fun getCalData(x: Int): T
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

class DynamicInvocationHandler<T : Any>(
        private val methodInvoke: (clazz: Class<T>, method: Method, args: Array<Any>) -> Any?
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        return methodInvoke(proxy::class.java as Class<T>, method, args)
    }
}

object ProxyFactory {
    fun <T : Any> newInstance(clazz: Class<T>, methodInvoker: (clazz: Class<T>, method: Method, args: Array<Any>) -> Any): Any? {
        return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), DynamicInvocationHandler(methodInvoker))
    }
}

abstract class TypeRef<T> {
    val javaType: Type
        get() {
            return (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        }
    val kType: KType
        get() {
            return this::class.supertypes.find { it.classifier == TypeRef::class }!!.arguments[0].type!!
        }
}

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val type = object : TypeRef<Task<Int>>() {}.javaType
        val task = ProxyFactory.newInstance((type as ParameterizedTypeImpl).rawType) { clazz: Class<*>, method: Method, args: Array<Any> ->
            var result: Any = Unit
            if (method.name == "setData") {
                println("${args[0]} Data is saved")
            } else if (method.name == "getCalData") {
                println(method.returnType)
                result = args[0] as Int * 10
            }
            result
        } as Task<*>
        task.setData("Test")
        println(task.getCalData(5))

//        val clazz = StringTask::class.java
//        if (Task::class.java.isAssignableFrom(clazz)) {
//            clazz.genericInterfaces.forEach {
//                if ((it as ParameterizedTypeImpl).rawType == Task::class.java) {
//                    val c = it.actualTypeArguments.get(0)
//                    println(c)
//                }
//            }
//        }
//        if (clazz.genericInterfaces.contains(Task::class.java)) {
//
//        }
    }
}