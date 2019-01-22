package sprink

import java.util.*

class Sprink {

    private val scopes: Stack<Scope> = Stack()

    init {
        scopes.add(Scope())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> instanceOf(klass: Class<out T>): T {
        return scopes.peek().instanceOf(klass)
    }

    fun <T : Any> bean(klass: Class<out T>, instance: T) {
        scopes.peek().bean(klass, instance)
    }

    fun <T : Any> bean(instance: T) {
        bean(instance::class.java, instance)
    }

    fun scope(init: Scope.() -> Unit): Scope {
        val scope = Scope()
        scope.init()
        return scope
    }
}

fun sprink(init: Sprink.() -> Unit): Sprink {
    val sprink = Sprink()
    sprink.init()
    return sprink
}