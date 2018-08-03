package cglib

import net.sf.cglib.proxy.MethodProxy
import net.sf.cglib.proxy.MethodInterceptor
import java.lang.reflect.Method
import net.sf.cglib.proxy.Enhancer


open class TargetObject {
    fun method1(paramName: String): String {
        return paramName
    }

    fun method2(count: Int): Int {
        return count
    }

    fun method3(count: Int): Int {
        return count
    }

    override fun toString(): String {
        return "TargetObject []$javaClass"
    }
}

/**
 * 拦截器
 */
class TargetInterceptor : MethodInterceptor {

    /**
     * 重写方法拦截在方法前和方法后加入业务
     * Object obj为目标对象
     * Method method为目标方法
     * Object[] params 为参数，
     * MethodProxy proxy CGlib方法代理对象
     */
    @Throws(Throwable::class)
    override fun intercept(obj: Any, method: Method, params: Array<Any>,
                           proxy: MethodProxy): Any {
        println("调用前")
        val result = proxy.invokeSuper(obj, params)
        println("调用后$result")
        return result
    }
}

fun main(args: Array<String>) {
    val enhancer = Enhancer()
    enhancer.setSuperclass(TargetObject::class.java)
    enhancer.setCallback(TargetInterceptor())
    val targetObject2 = enhancer.create() as TargetObject
    println(targetObject2)
    println(targetObject2.method1("mmm1"))
    println(targetObject2.method2(100))
    println(targetObject2.method3(200))
}