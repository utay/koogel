package sprink

import sprink.provider.AnyProvider
import sprink.provider.Singleton

class Scope {

    private val providers: HashMap<Any, AnyProvider<Any>> = hashMapOf()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> instanceOf(klass: Class<T>): T {
        val provider = providers[klass] ?: throw NoSuchElementException()
        return provider.getInstance(klass as Class<Any>) as T
    }

    fun <T : Any, U : T> bean(klass: Class<in T>, instance: U) {
        providers[klass] = Singleton(instance) as AnyProvider<Any>
    }

    fun <T : Any> bean(instance: T) {
        bean(instance.javaClass, instance)
    }

    fun <T : Any, U : T> bean(klass: Class<in T>, instance: U, init: Aspect<U>.() -> Unit): Aspect<U> {
        bean(klass, instance)
        val aspect = Aspect(instance)
        aspect.init()
        return aspect
    }

    fun <T : Any> addProvider(klass: Class<in T>, provider: AnyProvider<T>) {
        providers[klass] = provider as AnyProvider<Any>
    }
}
