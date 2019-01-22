package sprink.provider

class Singleton<out T>(private val inst: T) : Provider<T> {

    override fun getInstance(): T = inst
}