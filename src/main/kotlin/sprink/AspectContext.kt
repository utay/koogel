package sprink

import java.lang.reflect.Method

class AspectContext<T>(
    val target: T,
    val method: Method,
    val args: Array<out Any>?,
    val functions: List<(ctx: AspectContext<T>) -> Any?>
) {
    fun invoke(): Any? {
        val context = AspectContext(target, method, args, functions.subList(1, functions.size))
        return functions.first().invoke(context)
    }

    fun proceed(): Any? {
        return if (functions.isEmpty()) {
            if (args != null) {
                method.invoke(target, args)
            } else {
                method.invoke(target)
            }
        } else {
            invoke()
        }
    }
}