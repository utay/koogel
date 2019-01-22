package sprink

import kotlin.reflect.KClass

class Sprink {

    fun <T : Any> instanceOf(klass: Class<T>): T? {

        klass.constructors.forEach {
            if (it.parameterCount == 0) {
                return it.newInstance() as T?
            }
        }
        return null
    }

    fun <T: Any> bean(klass: Class<T>, instance: T) {

    }

    fun <T: Any> bean(instance: T) {

    }
}

fun sprink(init: Sprink.() -> Unit): Sprink {
    val sprink = Sprink()
    return sprink
}