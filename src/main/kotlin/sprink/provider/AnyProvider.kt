package sprink.provider

import sprink.Aspect
import java.lang.reflect.Proxy

abstract class AnyProvider<T> : Provider<T> {
    override fun getInstance(klass: Class<T>): T {
        val proxy = Proxy.newProxyInstance(
            klass.classLoader,
            Array(1) { klass as Class<*> },
            Aspect(createInstance())
        )
        return klass.cast(proxy)
    }

    abstract fun createInstance(): T
}
