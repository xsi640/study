package desgin

import java.lang.reflect.*
import java.lang.reflect.Proxy.newProxyInstance
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

interface Task<T> {
    fun setData(data: String)
    fun getCalData(x: Int): T
    fun find(t: T): T
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

class DynamicProxyInvocationHandler(val kType: KType, val invoker: Invoker) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
        return invoker(kType, method, args)
    }
}

typealias Invoker = (kType: KType, method: Method, args: Array<Any>) -> Any

object Proxy0 {
    fun newInstance(kType: KType, invoker: Invoker): Any {
        val clazz = (kType.classifier!! as KClass<*>).java
        return newProxyInstance(clazz.classLoader, arrayOf(clazz), DynamicProxyInvocationHandler(kType, invoker))
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
        val type = object : TypeRef<Task<Int>>() {}.kType
        val task = Proxy0.newInstance(type) { kType, method, args ->
            var result: Any = Unit
            if (method.name == "setData") {
                println("${args[0]} Data is saved")
            } else if (method.name == "getCalData") {
                kType.classifier
                result = args[0] as Int * 10
            }else if(method.name == "find"){
                kType.classifier
            }
            result
        } as Task<Int>


//        val task = ProxyFactory.newInstance((type as ParameterizedTypeImpl).rawType) { clazz: Class<*>, method: Method, args: Array<Any> ->
//            var result: Any = Unit
//            if (method.name == "setData") {
//                println("${args[0]} Data is saved")
//            } else if (method.name == "getCalData") {
//                println(method.returnType)
//                result = args[0] as Int * 10
//            }
//            result
//        } as Task<*>Task
        task.setData("Test")
        println(task.getCalData(5))
        println(task.find(11))

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