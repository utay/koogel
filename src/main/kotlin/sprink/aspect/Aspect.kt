package sprink.aspect

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class Aspect<T>(private val instance: T) : InvocationHandler {

    private val beforeConsumers = hashMapOf<Method, ArrayList<() -> Unit>>()
    private val afterConsumers = hashMapOf<Method, ArrayList<() -> Unit>>()
    private val aroundConsumers = hashMapOf<Method, ArrayList<(ctx: AspectContext<T>) -> Any?>>()

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        if (beforeConsumers.containsKey(method)) {
            beforeConsumers[method]?.forEach {
                it.invoke()
            }
        }

        val res: Any? = if (aroundConsumers.containsKey(method)) {
            val context = AspectContext(instance, method!!, args, aroundConsumers[method]!!)
            context.invoke()
        } else {
            if (args == null) {
                method?.invoke(instance)
            } else {
                method?.invoke(instance, args)
            }
        }

        if (afterConsumers.containsKey(method)) {
            afterConsumers[method]?.forEach {
                it.invoke()
            }
        }

        return res
    }

    fun before(method: Method?, consumer: () -> Unit) {
        method ?: return
        if (beforeConsumers[method] == null)
            beforeConsumers[method] = arrayListOf()
        beforeConsumers[method]?.add(consumer)
    }

    fun around(method: Method?, lambda: (ctx: AspectContext<T>) -> Any?) {
        method ?: return
        if (aroundConsumers[method] == null)
            aroundConsumers[method] = arrayListOf()
        aroundConsumers[method]?.add(lambda)
    }

    fun after(method: Method?, consumer: () -> Unit) {
        method ?: return
        if (afterConsumers[method] == null)
            afterConsumers[method] = arrayListOf()
        afterConsumers[method]?.add(consumer)
    }
}