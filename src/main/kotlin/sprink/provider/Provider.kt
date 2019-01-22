package sprink.provider

interface Provider<out T> {
    fun getInstance(): T
    fun clean() = {}
}