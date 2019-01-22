package sprink.provider

import java.util.function.Supplier

class Prototype<T>(private val supplier: Supplier<T>) : AnyProvider<T>() {
    override fun createInstance(): T = supplier.get()
}