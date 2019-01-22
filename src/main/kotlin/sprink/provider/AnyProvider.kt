package sprink.provider

import sprink.aspect.Aspect
import sprink.aspect.AspectContext
import java.lang.reflect.Method
import java.lang.reflect.Proxy

abstract class AnyProvider<T> : Provider<T> {

    var aspect: Aspect<T>? = null

    override fun getInstance(klass: Class<T>): T {
        val instance = createInstance()
        if (aspect == null) {
            aspect = Aspect(instance)
        }
        val proxy = Proxy.newProxyInstance(
            klass.classLoader,
            Array(1) { klass as Class<*> },
            aspect
        )
        return klass.cast(proxy)
    }

    fun before(method: Method?, consumer: () -> Unit) {
        aspect?.before(method, consumer)
    }

    fun around(method: Method?, lambda: (ctx: AspectContext<T>) -> Any?) {
        aspect?.around(method, lambda)
    }

    fun after(method: Method?, consumer: () -> Unit) {
        aspect?.after(method, consumer)
    }

    abstract fun createInstance(): T

    fun createAspectWithInstance(instance: T) {
        aspect = Aspect(instance)
    }
}
