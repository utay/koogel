package sprink.provider

class Singleton<T>(private val inst: T) : AnyProvider<T>() {
    override fun createInstance(): T = inst
}