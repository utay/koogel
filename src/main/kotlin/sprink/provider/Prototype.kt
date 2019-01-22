package sprink.provider

import java.util.function.Supplier

class Prototype<out T>(private val supplier: Supplier<T>) : Provider<T> {
    override fun getInstance(): T {
        return supplier.get()
    }
}