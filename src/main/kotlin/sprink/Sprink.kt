package sprink

import sprink.provider.Provider
import java.util.*

class Sprink {

    private val scopes: Stack<Scope> = Stack()

    init {
        scopes.add(Scope())
    }

    fun <T : Any> instanceOf(klass: Class<out T>): T {
        return scopes.peek().instanceOf(klass)
    }

    fun <T : Any, U : T> bean(klass: Class<in T>, instance: U) {
        scopes.peek().bean(klass, instance)
    }

    fun <T : Any> bean(instance: T) {
        bean(instance.javaClass, instance)
    }

    fun <T : Any> provider(klass: Class<T>, provider: Provider<T>) {
        scopes.peek().addProvider(klass, provider)
    }

    fun scope(init: Scope.() -> Unit): Scope {
        val scope = Scope()
        scope.init()
        scopes.add(scope)
        return scope
    }
}

fun sprink(init: Sprink.() -> Unit): Sprink {
    val sprink = Sprink()
    sprink.init()
    return sprink
}