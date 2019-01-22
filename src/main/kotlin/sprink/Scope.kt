package sprink

import sprink.provider.Provider
import sprink.provider.Singleton

class Scope {

    private val providers: HashMap<Any, Provider<Any>> = hashMapOf()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> instanceOf(klass: Class<in T>): T {
        val provider = providers[klass] ?: throw NoSuchElementException()
        return provider.getInstance() as T
    }

    fun <T : Any, U : T> bean(klass: Class<in T>, instance: U) {
        providers[klass] = Singleton(instance)
    }

    fun <T : Any> bean(instance: T) {
        bean(instance.javaClass, instance)
    }

    fun <T : Any> addProvider(klass: Class<in T>, provider: Provider<T>) {
        providers[klass] = provider
    }
}
