package sprink

import sprink.provider.AnyProvider
import sprink.provider.Provider
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

    fun <T : Any, U : T> bean(klass: Class<in T>, instance: U, init: AnyProvider<U>.() -> Unit): AnyProvider<U>? {
        bean(klass, instance)
        val provider = providers[klass] as AnyProvider<U>
        provider.createAspectWithInstance(instance)
        provider.init()
        return provider
    }

    fun <T : Any> addProvider(klass: Class<in T>, provider: AnyProvider<T>) {
        providers[klass] = provider as AnyProvider<Any>
    }

    fun clean() {
        providers.forEach {
            it.value.clean()
        }
    }
}
