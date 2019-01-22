package sprink

import sprink.provider.Provider
import sprink.provider.Singleton

class Scope {

    private val providers: HashMap<Class<out Any>, Provider<Any>> = hashMapOf()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> instanceOf(klass: Class<out T>): T {
        val provider = providers[klass]
        if (provider != null) {
            return provider.getInstance() as T
        }
        throw NoSuchElementException()
    }

    fun <T : Any> bean(klass: Class<out T>, instance: T) {
        providers[klass] = Singleton(instance)
    }

    fun <T : Any> bean(instance: T) {
        bean(instance::class.java, instance)
    }
}
