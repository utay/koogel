package sprink

class Sprink {

    private val providers: HashMap<Class<Any>, Provider> = hashMapOf()
    private val instances: HashMap<Class<out Any>, Any> = hashMapOf()

    fun <T : Any> instanceOf(klass: Class<T>): T? {

        klass.constructors.forEach {
            if (it.parameterCount == 0) {
                return it.newInstance() as T?
            }
        }
        return null
    }

    fun <T : Any> bean(klass: Class<out T>, instance: T) {
        instances[klass] = instance
    }

    fun <T : Any> bean(instance: T) {
        bean(instance::class.java, instance)
    }
}

fun sprink(init: Sprink.() -> Unit): Sprink {
    val sprink = Sprink()
    sprink.init()
    return sprink
}